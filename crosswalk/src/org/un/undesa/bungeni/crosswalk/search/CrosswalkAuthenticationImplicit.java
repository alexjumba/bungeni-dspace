package org.un.undesa.bungeni.crosswalk.search;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.log4j.Logger;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.dspace.authorize.AuthorizeException;
//import org.dspace.core.ConfigurationManager;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
//import org.dspace.core.LogManager;
import org.dspace.eperson.EPerson;
import org.dspace.authenticate.AuthenticationMethod;
import org.dspace.authenticate.AuthenticationManager;
import org.dspace.eperson.Group;
import java.util.Collection;

/**
 * A stackable authentication method
 * based on the DSpace internal "EPerson" database.
 * See the <code>AuthenticationMethod</code> interface for more details.
 * <p>
 * The <em>username</em> is the E-Person's email address,
 * and and the <em>password</em> (given to the <code>authenticate()</code>
 * method) must match the EPerson password.
 * <p>
 * This is the default method for a new DSpace configuration.
 * If you are implementing a new "explicit" authentication method,
 * use this class as a model.
 * <p>
 * You can use this (or another explict) method in the stack to
 * implement HTTP Basic Authentication for servlets, by passing the
 * Basic Auth username and password to the <code>AuthenticationManager</code>.
 *
 * @author Larry Stone
 * @version $Revision: 2168 $
 */
public class CrosswalkAuthenticationImplicit
    implements AuthenticationMethod {

    /** log4j category */
    //private static Logger log = Logger.getLogger(PasswordAuthentication.class);

    /**
     * Look to see if this email address is allowed to register.
     * <p>
     * The configuration key authentication.password.domain.valid is examined
     * in dspace.cfg to see what domains are valid.
     * <p>
     * Example - aber.ac.uk domains : @aber.ac.uk
     * Example - MIT domain and all .ac.uk domains: @mit.edu, .ac.uk
     */
    public boolean canSelfRegister(Context context,
                                   HttpServletRequest request,
                                   String email)
                                                 throws SQLException
    {
    	return true;
           
    }

    /**
     *  Nothing extra to initialize.
     */
    public void initEPerson(Context context, HttpServletRequest request,
            EPerson eperson)
        throws SQLException
    {
    	System.out.println("\n\nat initEPerson");
    }

    /**
     * We always allow the user to change their password.
     */
    public boolean allowSetPassword(Context context,
                                    HttpServletRequest request,
                                    String username)
        throws SQLException
    {
        return false;//true;
    }

    /**
     * This is an explicit method, since it needs username and password
     * from some source.
     * @return false
     */
    public boolean isImplicit()
    {
        return true;
    }

    /**
     * No special groups.
     */
    public int[] getSpecialGroups(Context context, HttpServletRequest request)
    {
        return new int[0];
    }

    /**
     * Check credentials: username must match the email address of an
     * EPerson record, and that EPerson must be allowed to login.
     * Password must match its password.  Also checks for EPerson that
     * is only allowed to login via an implicit method
     * and returns <code>CERT_REQUIRED</code> if that is the case.
     *
     * @param context
     *  DSpace context, will be modified (EPerson set) upon success.
     *
     * @param username
     *  Username (or email address) when method is explicit. Use null for
     *  implicit method.
     *
     * @param password
     *  Password for explicit auth, or null for implicit method.
     *
     * @param realm
     *  Realm is an extra parameter used by some authentication methods, leave null if
     *  not applicable.
     *
     * @param request
     *  The HTTP request that started this operation, or null if not applicable.
     *
     * @return One of:
     *   SUCCESS, BAD_CREDENTIALS, CERT_REQUIRED, NO_SUCH_USER, BAD_ARGS
     * <p>Meaning:
     * <br>SUCCESS         - authenticated OK.
     * <br>BAD_CREDENTIALS - user exists, but assword doesn't match
     * <br>CERT_REQUIRED   - not allowed to login this way without X.509 cert.
     * <br>NO_SUCH_USER    - no EPerson with matching email address.
     * <br>BAD_ARGS        - missing username, or user matched but cannot login.
     */
    public int authenticate(Context context,
                            String username,
                            String password,
                            String realm,
                            HttpServletRequest request)
        throws SQLException
    {
    	System.out.println("login started");
        
    	EPerson eperson = null;
    	
    	try
        {
            eperson = EPerson.findByEmail(context, "email");
            context.setCurrentUser(eperson);
        }
        catch (AuthorizeException e)
        {
            eperson = null;
        }
        
        if(eperson == null){
            //log.info(LogManager.getHeader(context, "authenticate", "attempting password auth of user="+username));
            context.setIgnoreAuthorization(true);
            try
            {
            	if (eperson == null)
                {
            		HttpClient client = new HttpClient();
            	    GetMethod method = new GetMethod(ConfigurationManager.getProperty("undesa.bungeni.auth.url")+"/authtoken.do");
            	    
            	    try{
            	    	// Provide custom retry handler is necessary
            			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
            			  		new DefaultHttpMethodRetryHandler(3, false));
            			String token = null;
            			for(Cookie cookie: request.getCookies()) {
            				if(cookie.getName().equalsIgnoreCase("token")) {
            					token = cookie.getValue();
            					break;
            				}
            			}
            			if(token == null) {
            				//return BAD_ARGS;
            				token = "1234567890";
            			} 
            			method.setQueryString(URIUtil.encodeQuery("token="+token));
            			
            			// Execute the method.
            	        int statusCode = client.executeMethod(method);

            	        if (statusCode != HttpStatus.SC_OK) {
            	          System.err.println("Method failed: " + method.getStatusLine());
            	        }

            	        // Read the response body.
            	        /*byte[]*/Header responseHeader = method.getResponseHeader("username");//.getResponseBody();
            	        eperson.setNetid(responseHeader.getValue().toLowerCase());
            	        System.out.println(responseHeader.getValue().toLowerCase());
            	    	
            	    } catch (HttpException e) {
              	        System.err.println("Fatal protocol violation: " + e.getMessage());
              	        e.printStackTrace();
              	      } catch (IOException e) {
              	        System.err.println("Fatal transport error: " + e.getMessage());
              	        e.printStackTrace();
              	      } finally {
              	        // Release the connection.
              	        method.releaseConnection();
              	      } 
                    // Need to create new eperson
                    // FIXME: TEMPORARILY need to turn off authentication, as usually
                    // only site admins can create e-people
                    
                    eperson = EPerson.create(context);
                    eperson.setEmail("email"+"@parliaments.info");
                    eperson.setNetid(/*netid*/null);
                    eperson.setFirstName("fname");
                    eperson.setLastName("lname");
                    eperson.setMetadata("phone", "075555555");
                    eperson.setCanLogIn(true);
                    
                    AuthenticationManager.initEPerson(context, request, eperson);
                    eperson.update();
                    context.commit();
                    context.setCurrentUser(eperson);
                    
                }

                eperson = EPerson.findByEmail(context, username.toLowerCase());
            }
            catch (AuthorizeException e)
            {
            	eperson = null;
                // ignore exception, treat it as lookup failure.
            }finally {
            	context.setIgnoreAuthorization(false);            	
            }

            // lookup failed.
            if (eperson == null)
                return NO_SUCH_USER;

            // cannot login this way
            else if (!eperson.canLogIn())
                return BAD_ARGS;

            // this user can only login with x.509 certificate
            else if (eperson.getRequireCertificate())
            {
                //log.warn(LogManager.getHeader(context, "authenticate", "rejecting PasswordAuthentication because "+username+" requires certificate."));
                return CERT_REQUIRED;
            }

            // login is ok if password matches:
            else if (eperson.checkPassword(password))
            {
                context.setCurrentUser(eperson);
                //log.info(LogManager.getHeader(context, "authenticate", "type=PasswordAuthentication"));
                return SUCCESS;
            }
            else
                return BAD_CREDENTIALS;
        }
        if (eperson == null)
        {
            return AuthenticationMethod.NO_SUCH_USER;
        }
        else
        {
            // the person exists, just return ok
            context.setCurrentUser(eperson);
        }

        return AuthenticationMethod.SUCCESS;
    }

    /**
     * Returns URL of password-login servlet.
     *
     * @param context
     *  DSpace context, will be modified (EPerson set) upon success.
     *
     * @param request
     *  The HTTP request that started this operation, or null if not applicable.
     *
     * @param response
     *  The HTTP response from the servlet method.
     *
     * @return fully-qualified URL
     */
    public String loginPageURL(Context context,
                            HttpServletRequest request,
                            HttpServletResponse response)
    {
        return response.encodeRedirectURL(request.getContextPath() +
                                          "/password-login");
    }

    /**
     * Returns message key for title of the "login" page, to use
     * in a menu showing the choice of multiple login methods.
     *
     * @param context
     *  DSpace context, will be modified (EPerson set) upon success.
     *
     * @return Message key to look up in i18n message catalog.
     */
    public String loginPageTitle(Context context)
    {
        return "org.dspace.eperson.PasswordAuthentication.title";
    }
    
    /** Find dspaceGroup in DSpace database, if found, include it into groups */
    private void addGroup(Collection groups, Context context, String dspaceGroup)
    {
        try
        {
            Group g = Group.findByName(context, dspaceGroup);
            if (g == null)
            {
                // oops - no group defined
                groups.add(new Integer(0));
            }
            else
            {
                groups.add(new Integer(g.getID()));
            }
        }
        catch (SQLException e)
        {
            //log.error("Mapping group:" + dspaceGroup + " failed with error", e);
        	e.printStackTrace();
        }
    }
}
