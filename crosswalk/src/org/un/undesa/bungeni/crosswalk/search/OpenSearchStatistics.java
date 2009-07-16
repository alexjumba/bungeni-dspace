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
import java.util.Map;

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
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.dspace.app.webui.util.UIUtil;

public class OpenSearchStatistics extends HttpServlet
{
	
	protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
		System.out.println("\n\ngotten to doget"+request.getRequestURI());
		Context context = null;  		
		
    	try{context = getContext(request);}catch(Exception ex) {ex.printStackTrace();}
    	
    	Abdera abdera = new Abdera();
		Feed feed = abdera.newFeed();
		//IntegerElement ie = feed.addExtension(OpenSearchConstants.ITEMS_PER_PAGE);
		//ie.setValue(10);

		//ie = feed.addExtension(OpenSearchConstants.START_INDEX);
		//ie.setValue(0);

		//ie = feed.addExtension(OpenSearchConstants.TOTAL_RESULTS);
		//ie.setValue(qrs.getHitHandles().size());
		
		feed.setId("tag:example.org,2007:/foo");
		feed.setTitle("Test Feed");
		feed.setSubtitle("Feed subtitle");
		feed.setUpdated(new Date());
		feed.addAuthor("Alex Jumba");
		feed.addLink("http://alex.com");
		feed.addLink("http://alex.com/foo","self");
    	
    	DSpaceObject resultDSO=null;
    	//Map<Integer, Map<String, Integer>> mp2 = new HashMap<Integer, Map<String, Integer>>();
    	//Map<String,Integer> mp = new HashMap<String,Integer>();
    	IterableMap mp2 = new HashedMap();    	
    	for(int i = 1; i <= 300;i++) {	
				IterableMap mp = new HashedMap();
				//System.out.println("\nreached jloop");
				String query = "(govdocument:"+i+")";
				//System.out.println("\n"+query);
				//System.out.println("\n\nquery: "+query);
				QueryResults qrs1 = null;
				QueryArgs qa = new QueryArgs();
				qa.setQuery(query);
				qa.setPageSize(2000);
				qa.setStart(0);
				qa.setSortOrder(SortOption.DESCENDING);
				qrs1 = DSQuery.doQuery(context, qa);
				java.util.List<String> handles1 = qrs1.getHitHandles();
				for (String handle : handles1)
		        {
					resultDSO = null;
					try {
						resultDSO = HandleManager.resolveToObject(context, handle);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
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
	            		String cl = null;
	            		try {
							cl = it.getCollections()[0].getHandle();
							System.out.println("\n\nhaaanle:"+cl);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            		if(mp.containsKey(cl)){
	            			Integer count = (Integer) mp.get(cl);
	            			count++;
	            			mp.put(cl, count);
	            			System.out.println("containskey"+cl+" cnt:"+count);
	            		}else{
	            			mp.put(cl, new Integer(1));
	            			System.out.println("nocontainskey"+cl+" cnt:"+1);
	            		}
	            		//Entry entry = feed.addEntry();
						//entry = fillEntry(entry, it);	                    
	            		
	            		break;
					}
		        }
				mp2.put(new Integer(i), mp);
		}
    	
    	//System.out.println("@mi");
    	MapIterator it = mp2.mapIterator();
  		while (it.hasNext()) {
  			//System.out.println("@it loop");
  		   Integer parliament = (Integer)it.next();
  		 //System.out.println("@it b4 hc");
  		   IterableMap handleCounter = (HashedMap)it.getValue();
  		 //System.out.println("@it afta hc");
  		   MapIterator it2 = handleCounter.mapIterator();
  		   while (it2.hasNext()) {
  			 //System.out.println("@it2 loop");
  			   String hand = (String)it2.next();
    		   Integer counter = (Integer)it2.getValue();
  	  		   	
  	  		   
    		   DSpaceObject resultDSO2=null;
			try {
				resultDSO2 = HandleManager.resolveToObject(context, hand);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			switch(resultDSO2.getType()) {
			case Constants.COLLECTION:
				//System.out.println("@collection");
	    		org.dspace.content.Collection col = (org.dspace.content.Collection) resultDSO2;
	    		Entry entry = feed.addEntry();
	    		String xml = "<parliament>"+parliament+"</parliament><collection>"+col.getMetadata("name")+"</collection><count>"+counter+"</count>";
	    		xml = "<dt></dt><dd>"+xml+"</dd>";
	  		  	String content = "<div xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
	  		  		"<dl class=\"xoxo\" >" +/*StringEscapeUtils.escapeXml(xml)*/xml +"</dl></div>";
	  		  	entry.setContentAsXhtml(content);
	    		break;
			}  		  		  
  		   }
  		}    	
    	
  		//System.out.println("@bfore os");
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
	
}
