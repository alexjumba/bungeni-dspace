package org.un.undesa.bungeni.crosswalk.search;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.log4j.Logger;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dspace.authorize.AuthorizeException;
//import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
//import org.dspace.core.LogManager;
import org.dspace.eperson.EPerson;
import org.dspace.authenticate.AuthenticationMethod;
import org.dspace.core.ConfigurationManager;

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
public class CrosswalkAuthentication
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
        return false;
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
        if (username != null && password != null)
        {
            EPerson eperson = null;
            //log.info(LogManager.getHeader(context, "authenticate", "attempting password auth of user="+username));
            
            HttpClient client = new HttpClient();
    	    GetMethod method = new GetMethod(ConfigurationManager.getProperty("undesa.bungeni.auth.url")+"/login");
    	    String responsible = "";
    	    try{
    	    	// Provide custom retry handler is necessary
    			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
    			  		new DefaultHttpMethodRetryHandler(3, false));
    			// Execute the method.
    	        int statusCode = client.executeMethod(method);

    	        if (statusCode != HttpStatus.SC_OK) {
    	          System.err.println("Method failed: " + method.getStatusLine());
    	        }

    	        // Read the response body.
    	        byte[] responseBody = method.getResponseBody();

    	        // Deal with the response.
    	        // Use caution: ensure correct character encoding and is not binary data
    	        responsible = new String(responseBody);
    	        //System.out.println(responsible);
    	    	
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
    	    
            try
            {
                eperson = EPerson.findByEmail(context, username.toLowerCase());
            }
            catch (AuthorizeException e)
            {
                // ignore exception, treat it as lookup failure.
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

        // BAD_ARGS always defers to the next authentication method.
        // It means this method cannot use the given credentials.
        else
            return BAD_ARGS;
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
}
