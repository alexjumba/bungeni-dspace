package org.un.undesa.bungeni.crosswalk.search;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import org.dspace.sort.SortOption;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.core.Context;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.authorize.AuthorizeException;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import org.dspace.app.webui.util.JSPManager;

import org.dspace.storage.rdbms.TableRowIterator;
import org.dspace.content.*;
import org.dspace.content.Collection;

import com.thoughtworks.xstream.*;
import java.io.*;
import org.dspace.search.*;

import java.util.*;
import org.dspace.core.Constants;
import org.dspace.handle.HandleManager;
import org.apache.abdera.Abdera;
import org.apache.abdera.ext.opensearch.IntegerElement;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.commons.lang.StringEscapeUtils;
import org.dspace.app.webui.util.UIUtil;

public class OpenSearch extends HttpServlet
{
	
	protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
		System.out.println("\n\ngotten to doget"+request.getRequestURI());
		Context context = null;    	
		QueryResults qrs = null;		
		
    	try{context = getContext(request);}catch(Exception ex) {ex.printStackTrace();}
    	try {
    		qrs = search(request, context);
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	Abdera abdera = new Abdera();
		Feed feed = abdera.newFeed();
		IntegerElement ie = feed.addExtension(OpenSearchConstants.ITEMS_PER_PAGE);
		ie.setValue(10);

		ie = feed.addExtension(OpenSearchConstants.START_INDEX);
		ie.setValue(0);

		ie = feed.addExtension(OpenSearchConstants.TOTAL_RESULTS);
		ie.setValue(qrs.getHitCount()/*.getHitHandles().size()*/);
		
		feed.setId("tag:alex.org,2007:/foo");
		feed.setTitle("Test Feed");
		feed.setSubtitle("Feed subtitle");
		feed.setUpdated(new Date());
		feed.addAuthor("Alex Jumba");
		feed.addLink("http://alex.com");
		feed.addLink("http://alex.com/foo","self");
    	
    	DSpaceObject resultDSO=null;
    	java.util.List<String> handles = qrs.getHitHandles();
    	try {
	        for (String handle : handles)
	        {
	        	System.out.println("\n\nhandle ="+handle);
	            resultDSO = HandleManager.resolveToObject(context, handle);
	            System.out.println("\n\ntypee is:"+resultDSO.getType()+" constants item:"+Constants.ITEM);
	            switch(resultDSO.getType()) {
	            	case Constants.ITEM:
	            		System.out.println("\n\ntype is:"+resultDSO.getType()+"   handle is:"+resultDSO.getHandle());
	            		HarvestedItemInfo hif=null;
	                	try {
	                		hif = Harvest.getSingle(context, resultDSO.getHandle(), true);
	                	}catch(SQLException ex) {}
	            			
	            		if (hif == null)
	                    {
	            			System.out.println("\n\nhif null\n");
	            			break;
	                        //throw new IdDoesNotExistException(identifier);
	                    }
	                    else
	                    {
	                        if (hif.withdrawn)
	                        {
	                        	System.out.println("\n\nhif withdrawn\n\n");
	                        	break;
	                            //throw new NoMetadataFormatsException();
	                        }
	                        else
	                        {
	                        	System.out.println("\n\nhif not withdrawn\n");
	                            //return getRecordFactory().getSchemaLocations(hif);
	                        }
	                    }
	            		Item it = ((HarvestedItemInfo)hif).item;
	            		
	            		Entry entry = feed.addEntry();
						entry = fillEntry(entry, it, request);	                    
	            		
	            		break;
	            }
	            
	        }
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	OutputStream os = null;
        response.setContentType("text/xml");
		try {
			os = response.getOutputStream();
			abdera.getWriter().writeTo(feed, os);
			os.flush();
			if(os!=null) os.close();
		}catch(Exception e) {
			e.printStackTrace();			
		}	
    }
	
	private Context getContext(HttpServletRequest request) throws Exception{
        //Context context = null;

        // set all incoming encoding to UTF-8
        request.setCharacterEncoding("UTF-8");
        
        Context c = (Context) request.getAttribute("dspace.context");        

        if (c == null)
        {
            // No context for this request yet
            c = new Context();
            HttpSession session = request.getSession();

            // See if a user has authentication
            Integer userID = (Integer) session.getAttribute(
                    "dspace.current.user.id");
            // Set the session ID and IP address
            c.setExtraLogInfo("session_id=" + request.getSession().getId() + ":ip_addr=" + request.getRemoteAddr());

            // Store the context in the request
            request.setAttribute("dspace.context", c);
        }

        return c;
	}
	
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
		
    	doGet(request, response);
    }
	
	private QueryResults search(HttpServletRequest hsr, Context ctx)throws Exception {		
		QueryArgs qa = new QueryArgs();
		QueryArgs qat = new QueryArgs();
		try {
			qa.setPageSize(Integer.parseInt(hsr.getParameter("pagesize")));
			qat.setPageSize(Integer.parseInt(hsr.getParameter("pagesize")));
		}catch(Exception ex) {
			ex.printStackTrace();
			qa.setPageSize(20);
			qat.setPageSize(20);
		}
		try {
			qa.setStart(Integer.parseInt(hsr.getParameter("start")));
			qat.setStart(Integer.parseInt(hsr.getParameter("start")));
		}catch(Exception ex) {
			ex.printStackTrace();
			qa.setStart(0);
			qat.setStart(0);
		}
		SortOption sortOption = null;
        String order = hsr.getParameter("order");
        try {
        	int sortby = 2;
        	if(!hsr.getParameter("sort_by").equalsIgnoreCase("id")) {
    	    	String sb = hsr.getParameter("sort_by");    	    	
    	    	if(sb.equalsIgnoreCase("title")) {
    	    		sortby=1;
    	    		System.out.println("\n\ntitle");
    	    	}else if(sb.equalsIgnoreCase("issue_date")) {
    	    		sortby=2;
    	    		System.out.println("\n\nissue_date");
    	    	}else if(sb.equalsIgnoreCase("author")) {
    	    		sortby=5;
    	    		System.out.println("\n\nauthor");
    	    	}else if(sb.equalsIgnoreCase("description")) {
    	    		sortby=4;
    	    		System.out.println("\n\ndescription");
    	    	}
    	    	sortOption = SortOption.getSortOption(sortby);
    	    	qa.setSortOption(sortOption);
    	    	qat.setSortOption(sortOption);
    	    }
    	    if (SortOption.ASCENDING.equalsIgnoreCase(order))
    	    {
    	        qa.setSortOrder(SortOption.ASCENDING);
    	        qat.setSortOrder(SortOption.ASCENDING);
    	        System.out.println("\n\nascending");
    	    }
    	    else
    	    {
    	        qa.setSortOrder(SortOption.DESCENDING);
    	        qat.setSortOrder(SortOption.DESCENDING);
    	        System.out.println("\n\ndescending");
    	    }
        }catch(Exception ex) {
        	ex.printStackTrace();
        }
		
		qa.setQuery(qa.buildQuery(hsr));
		/*
		 * add date range logic
		 */
		
		String querystring = qa.getQuery();
		int last = querystring.lastIndexOf(")");
		System.out.println("\n\nQuery String"+querystring+" >psize:"+qat.getPageSize());
		
		System.out.println("\n\nlastindexof:"+last+"zz");
		try {
			if((!hsr.getParameter("parliament").equalsIgnoreCase("null")||hsr.getParameter("parliament")!=null) && !hsr.getParameter("parliament").contains("/")) {
				if(last == 1){
					querystring = "(govdocument:"+hsr.getParameter("parliament")+")";
				}else{
					querystring = querystring.substring(0, last-1)+" govdocument:"+hsr.getParameter("loc")+"))";
				}
				
			}			
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		last = querystring.lastIndexOf(")");
		try {
			if(!hsr.getParameter("range").equalsIgnoreCase("null")||hsr.getParameter("range")!=null) {
				querystring = querystring.substring(0, last)+" issue_date:["+hsr.getParameter("range")+"]"+")";
			}			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
		System.out.println("\n\nQuery String"+querystring);
		qa.setQuery(querystring);
		
		String query = "(govdocument: 29 issue_date:[1900 TO 2099])";
		//System.out.println("\n\nquery: "+query);
		QueryResults qrst = null;
//		QueryArgs qat = new QueryArgs();
		qat.setQuery(/*query*/querystring);
		//qat.setPageSize(2000);
		//qat.setStart(0);
		//qat.setSortOrder(SortOption.DESCENDING);
////		qrst = DSQuery.doQuery(ctx, qat);
		/*java.util.List<String> handles = qrs.getHitHandles();
		if(!handles.isEmpty()) {
			
		}*/
		
		String loc = "z";
		try {
			loc=hsr.getParameter("loc");
		}catch(Exception ex) {
			loc="z";
			ex.printStackTrace();
		}
		QueryResults qrs = null;
		if(loc!=null && !loc.equalsIgnoreCase("") && !loc.equalsIgnoreCase("z") && !loc.equalsIgnoreCase("null")) {
			System.out.println("\n\n\nloc not null : '"+loc+"'");
			org.dspace.content.Collection cll = null;
			Community comm = null;
			try {
				cll = (org.dspace.content.Collection)HandleManager.resolveToObject(getContext(hsr), loc);
				//qrs = DSQuery.doQuery(ctx, qa, cll);
				qrst = DSQuery.doQuery(ctx, qat, cll);
			}catch(Exception ex) {
				ex.printStackTrace();
				try {
					comm = (Community)HandleManager.resolveToObject(getContext(hsr), loc);
					//qrs = DSQuery.doQuery(ctx, qa, comm);
					qrst = DSQuery.doQuery(ctx, qat, comm);
				}catch(Exception exx) {
					exx.printStackTrace();
				}
				//just in case
				//qrs = DSQuery.doQuery(ctx, qa);
				qrst = DSQuery.doQuery(ctx, qat);
			}
		}else {
			//qrs = DSQuery.doQuery(ctx, qa);
			qrst = DSQuery.doQuery(ctx, qat);
		}
		System.out.println("\n\nEventual query is:"+qat.getQuery());
	
		
		
		
		return qrst;
	}
	
	public Entry fillEntry(Entry entry, Item item, HttpServletRequest request) {
		DCDate dd = null;
	  	  DCValue val = null;
	  	  DCValue[] dcvv = item.getMetadata("dc", "date", "issued", Item.ANY);
	  	  if(dcvv.length > 0) {  		  
	  		  dd = new DCDate(dcvv[0].value);
	  		  entry.setPublished(dd.toDate());
	  	  }
	  	  dcvv = item.getMetadata("dc", "contributor", "author", Item.ANY);
	  	  if(dcvv.length > 0) {
	  		  String str[]= {""};
	  		  try {
	  			  for(int i=0;i<dcvv.length;i++) {
		    			  str[i]=dcvv[i].value;  
		    		  }	   
	  		  }catch(Exception ex) {
	  			  ex.printStackTrace();
	  		  }	   
		  		String string = "";
				for(String strg: str) {
					string = string + ", "+ strg + " ";
				}
				entry.addAuthor(string);
	  	  }
	  	  dcvv = item.getMetadata("dc", "title", Item.ANY, Item.ANY);
	  	  if(dcvv.length > 0) {
			  entry.setTitle(dcvv[0].value);
	  	  }
	  	  dcvv = item.getMetadata("dc", "description", Item.ANY, Item.ANY);
	  	  if(dcvv.length > 0) {
	  		  String str[]= {""};
	  		  try {
	  			  for(int i=0;i<dcvv.length;i++) {
		    			  str[i]=StringEscapeUtils.escapeXml(dcvv[i].value);  
		    			  System.out.println("\n\n\ndc.decsrpition is:"+str[i]);
		    		  }	
	  		  }catch(Exception ex) {
	  			  ex.printStackTrace();
	  		  }	    		   
	  		String string = "";
			for(String strg: str) {
				string = string + ", "+ strg + " ";
			}
			//System.out.println("\n\n\n\nzxcvbnm,String: "+ string);
			
			entry.setSummary(string);
	  	  }
	  	dcvv = item.getMetadata("dc", "language", "iso", Item.ANY);
	  	  if(dcvv.length > 0) {
	  		  String str[]= {""};
	  		  try {
	  			  for(int i=0;i<dcvv.length;i++) {
		    			  str[i]=StringEscapeUtils.escapeXml(dcvv[i].value);  
		    			  System.out.println("\n\n\ndc.decsrpition is:"+str[i]);
		    		  }	
	  		  }catch(Exception ex) {
	  			  ex.printStackTrace();
	  		  }	    		   
	  		String string = "";
			for(String strg: str) {
				string = string + ", "+ strg + " ";
			}
			
			entry.setLanguage(string);
	  	  }
	  	  
	  	  dcvv = item.getMetadata("dc", "identifier", "uri", Item.ANY);
	  	  if(dcvv.length > 0) {
	  		  String str[] = dcvv[0].value.split("/");	      		
		  		  entry.setId(str[3]+"/"+str[4]);
	  	  }
	  	  String innerContent = "";
	  	dcvv = item.getMetadata("dc", "identifier", "govdoc", Item.ANY);
	  	  if(dcvv.length > 0) {
	  		  String str = StringEscapeUtils.escapeXml(dcvv[0].value);
	  		  innerContent = innerContent+"<dt></dt><h1>"+str+"</h1>";
	  	  }else{
	  		  innerContent = innerContent+"<dt></dt><h1></h1>";
	  	  }
	  	  
	  	  String col = "";
	  	  try {
			col = item.getCollections()[0].getMetadata("name");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	  innerContent = innerContent+"<dt></dt><h2>"+col+"</h2>";
	  	  
	  	  
	  	  
	  	try {
  		  Bundle[] bundles = item.getBundles("ORIGINAL");

	          	if (bundles.length == 0)
	          	{
	          		
	          	}
	          	else
	          	{
	          		boolean html = false;
	        		String handle = item.getHandle();
	        		Bitstream primaryBitstream = null;

	        		Bundle[] bunds = item.getBundles("ORIGINAL");
	        		Bundle[] thumbs = item.getBundles("THUMBNAIL");
	          	// if item contains multiple bitstreams, display bitstream
	        		// description
	        		boolean multiFile = false;
	        		Bundle[] allBundles = item.getBundles();

	        		for (int i = 0, filecount = 0; (i < allBundles.length)
	                    	&& !multiFile; i++)
	        		{
	        			filecount += allBundles[i].getBitstreams().length;
	        			multiFile = (filecount > 1);
	        		}

	        		/*
	        		// check if primary bitstream is html
	        		if (bunds[0] != null)
	        		{
	        			Bitstream[] bits = bunds[0].getBitstreams();

	        			for (int i = 0; (i < bits.length) && !html; i++)
	        			{
	        				if (bits[i].getID() == bunds[0].getPrimaryBitstreamID())
	        				{
	        					html = bits[i].getFormat().getMIMEType().equals(
	        							"text/html");
	        					primaryBitstream = bits[i];
	        				}
	        			}
	        		}*/
	        		List<TransferMap> ltm = new ArrayList<TransferMap>();
	        			for (int i = 0; i < bundles.length; i++)
	            		{
	        				System.out.println("fsdfws\n"+request.getContextPath());
	        				TransferMap tm = new TransferMap();
	        				
	            			Bitstream[] bitstreams = bundles[i].getBitstreams();

	            			for (int k = 0; k < bitstreams.length; k++)
	            			{
	            				// Skip internal types
	            				if (!bitstreams[k].getFormat().isInternal())
	            				{

	                                // Work out what the bitstream link should be
	                                // (persistent
	                                // ID if item has Handle)
	                                String bsLink = "<a target=\"_blank\" href=\""
	                                        + request.getContextPath();

	                                if ((handle != null)
	                                        && (bitstreams[k].getSequenceID() > 0))
	                                {
	                                    bsLink = bsLink + "/bitstream/"
	                                            + item.getHandle() + "/"
	                                            + bitstreams[k].getSequenceID() + "/";
	                                }
	                                else
	                                {
	                                    bsLink = bsLink + "/retrieve/"
	                                            + bitstreams[k].getID() + "/";
	                                }

	                                bsLink = bsLink
	                                        + UIUtil.encodeBitstreamName(bitstreams[k]
	                                            .getName(),
	                                            Constants.DEFAULT_ENCODING) + "\">";

	            					bsLink += bitstreams[k].getName()+" "+(bitstreams[k].getDescription() == null? "":bitstreams[k].getDescription())+"  "+UIUtil.formatFileSize(bitstreams[k].getSize())+"  "+bitstreams[k].getFormatDescription()+"</a>";
	            					tm.setKey(bsLink);
	            					ltm.add(tm);
	            				}
	            			}
	            		}
	        			String st = "<ul class=\"bitstreams\">";
	        			for(TransferMap tm: ltm) {
	        				//st+="<li><a href=\""+tm.getValue()+"\">"+tm.getKey()+"</a>";
	        				st +="<li>"+tm.getKey()+"</li>";
	        			}
	        			st+="</ul>";
	        			innerContent = innerContent+"<dt></dt><h3>"+st+"</h3>";
	        		
	          	}
  	  }catch(Exception ex) {
  		  innerContent = innerContent+"<dt></dt><h3></h3>";
  		  ex.printStackTrace();	    		  
  	  }
	  	  
	  	  
	  	  
	  	  
	  	  String content = "<div xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
	  	  		"<dl class=\"xoxo\" >" +innerContent +"</dl></div>";
	  	  entry.setContentAsXhtml(content);
	  	  
	  	  return entry;
	}
	
}
