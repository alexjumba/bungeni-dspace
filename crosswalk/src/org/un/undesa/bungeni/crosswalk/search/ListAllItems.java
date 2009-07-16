package org.un.undesa.bungeni.crosswalk.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.dspace.core.Context;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DCDate;
import org.dspace.content.DCValue;
import org.dspace.content.ItemIterator;
import org.dspace.core.Context;
import org.dspace.core.LogManager;

import com.thoughtworks.xstream.XStream;
import org.dspace.content.Item;
import org.apache.abdera.*;
import org.apache.abdera.ext.json.*;
import org.apache.abdera.ext.media.*;
import org.apache.abdera.ext.opensearch.*;
import org.apache.abdera.ext.serializer.impl.*;
import org.apache.abdera.ext.sharing.*;
import org.apache.abdera.model.*;
import java.io.*;

public class ListAllItems extends HttpServlet {
	protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
		//prepare(request, response);
		
		System.out.println("\n\nreached listAllItems");
		
        Community[] communities = null;
        try {
        	System.out.println("\n\nin findall try");
        	communities = Community.findAllTop(getContext(request));
        	System.out.println("\n\nin findall try after");
        }catch(Exception ex) {
        	System.out.println("\n\nin findall catch");
        	ex.printStackTrace();
        }

        Abdera abdera = new Abdera();
		Feed feed = abdera.newFeed();
        try {
        	feed=collect(feed, communities);
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
        
        /*
    	XStream xstream = new XStream();
        String xml = xstream.toXML(ccl);
    	
        System.out.println("\n\n\nExtractRecord doGet ->"+xml);
        
        byte b[]= xml.getBytes();
        //byte c[] = new byte[100];
        //byte b2[]= xml.getBytes();
        System.out.println("xml length is -> "+xml.length());
        if(xml.length()==7) {
        	System.out.println("\n\nxml 7");
        }
        ByteArrayInputStream bif= new ByteArrayInputStream(b);
        System.out.println("\n\n\nxml2");
        OutputStream stream=response.getOutputStream();
        System.out.println("\n\n\nxml3");
        response.setContentType("text/xml");
        System.out.println("\n\n\nxml1");
        
        int nextChar;
        while ( ( nextChar = bif.read() ) != -1  ) {
        	//System.out.println("\n\n\nxml-w1");
        	stream.write(nextChar);
        	//stream.write( '\n' );
        	stream.flush();
        }
        System.out.println("\n\n\nxml4");
        if(bif!=null)bif.close();
        System.out.println("\n\n\nxml6");
        if(stream!=null) stream.close();
        System.out.println("\n\n\nxml5");*/
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
	
	private Feed collect(Feed feed, Community[] communities) throws Exception { 
		
		IntegerElement ie = feed.addExtension(OpenSearchConstants.ITEMS_PER_PAGE);
		ie.setValue(10);

		ie = feed.addExtension(OpenSearchConstants.START_INDEX);
		ie.setValue(0);

		ie = feed.addExtension(OpenSearchConstants.TOTAL_RESULTS);
		ie.setValue(100);

		feed.setId("tag:example.org,2007:/foo");
		feed.setTitle("Test Feed");
		feed.setSubtitle("Feed subtitle");
		feed.setUpdated(new Date());
		feed.addAuthor("James Snell");
		feed.addLink("http://example.com");
		feed.addLink("http://example.com/foo","self");
		
        for(Community cm : communities) {        	
			if(cm.getSubcommunities().length == 0){
				System.out.println("\n\n\n\nNo subcommunities");
				for(org.dspace.content.Collection cn2: cm.getCollections()) {					
					ItemIterator ii = cn2.getAllItems();
					feed.addCategory("", cn2.getHandle(), cn2.getName());
					while(ii.hasNext()){
						Item it = (Item) ii.next();
						
						Entry entry = feed.addEntry();
						entry = fillEntry(entry, it);
						/*entry.setId("tag:example.org,2007:/foo/entries/1");
						entry.setTitle("Entry title");
						entry.setSummaryAsHtml("<p>This is the entry title</p>");
						entry.setUpdated(new Date());
						entry.setPublished(new Date());
						entry.addLink("http://example.com/foo/entries/1");*/							
					}
		    	 }
			}else{	
				for(org.dspace.content.Community cn: cm.getSubcommunities()) {
					for(org.dspace.content.Collection cn2: cn.getCollections()) {
				    	ItemIterator ii = cn2.getAllItems();
				    	feed.addCategory("", cn2.getHandle(), cn2.getName());
						while(ii.hasNext()){
							Item it = (Item) ii.next();
							
							Entry entry = feed.addEntry();
							entry = fillEntry(entry, it);
							/*entry.setId("tag:example.org,2007:/foo/entries/1");
							entry.setTitle("Entry title");
							entry.setSummaryAsHtml("<p>This is the entry title</p>");
							entry.setUpdated(new Date());
							entry.setPublished(new Date());
							entry.addLink("http://example.com/foo/entries/1");*/							
						}
		    	    }	
				}	        		
		    }	
	   } 
        return feed;
	}
	
	public Entry fillEntry(Entry entry, Item item) {
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
			
			entry.setSummary(string);
	  	  }
	  	  dcvv = item.getMetadata("dc", "identifier", "uri", Item.ANY);
	  	  if(dcvv.length > 0) {
	  		  String str[] = dcvv[0].value.split("/");	      		
		  		  entry.setId(str[3]+"/"+str[4]);
	  	  }
	  	  return entry;
	}
	
	public void prepare(HttpServletRequest request, HttpServletResponse response){
		Abdera abdera = new Abdera();
		Feed feed = abdera.newFeed();
		IntegerElement ie = feed.addExtension(OpenSearchConstants.ITEMS_PER_PAGE);
		ie.setValue(10);

		ie = feed.addExtension(OpenSearchConstants.START_INDEX);
		ie.setValue(0);

		ie = feed.addExtension(OpenSearchConstants.TOTAL_RESULTS);
		ie.setValue(100);

		feed.setId("tag:example.org,2007:/foo");
		feed.setTitle("Test Feed");
		feed.setSubtitle("Feed subtitle");
		feed.setUpdated(new Date());
		feed.addAuthor("James Snell");
		feed.addLink("http://example.com");
		feed.addLink("http://example.com/foo","self");

		Entry entry = feed.addEntry();
		entry.setId("tag:example.org,2007:/foo/entries/1");
		entry.setTitle("Entry title");
		entry.setSummaryAsHtml("<p>This is the entry title</p>");
		entry.setUpdated(new Date());
		entry.setPublished(new Date());
		entry.addLink("http://example.com/foo/entries/1");
		
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			abdera.getWriter().writeTo(feed, os);
			os.flush();
			if(os!=null) os.close();
		}catch(Exception e) {
			e.printStackTrace();			
		}		
	}
}