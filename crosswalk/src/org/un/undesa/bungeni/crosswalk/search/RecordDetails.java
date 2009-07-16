package org.un.undesa.bungeni.crosswalk.search;

import javax.servlet.http.HttpServlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.thoughtworks.xstream.*;

import java.io.*;
import org.dspace.search.*;
import java.util.*;
import org.dspace.core.Constants;
import org.dspace.handle.HandleManager;


public class RecordDetails extends HttpServlet {
	protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
		DSpaceObject resultDSO = null;
		try {
			resultDSO = HandleManager.resolveToObject(getContext(request), request.getParameter("url"));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		Item item = (Item)resultDSO;
		XStream xstream = new XStream();
		SearchResult sr = itemDetails(item);
    	if(sr==null) {
    		sr = new SearchResult();
    	}
        String xml = xstream.toXML(sr);
        
        System.out.println("\n\n\nExtractRecord doGet ->"+xml);
        
        byte b[]= xml.getBytes();
        //byte c[] = new byte[100];
        //byte b2[]= xml.getBytes();
        System.out.println("xml length is -> "+xml.length());
        if(xml.length()==7) {
        	System.out.println("\n\nxml 7");
        }
        ByteArrayInputStream bif= new ByteArrayInputStream(b/*2*/);
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
        System.out.println("\n\n\nxml5");
		
    }
	
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
		doGet(request, response);
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
	
	private SearchResult itemDetails(Item item) {
		
		

  	  //SearchResult sr = new SearchResult();
  	  //sr.setAuthor(item.getDC( "author", Item.ANY, Item.ANY ).toString());
	    	// Get all the DC
	        DCValue[] allDC = item.getDC(Item.ANY, Item.ANY, Item.ANY);
	      StringBuffer metadata1 = new StringBuffer();
	  	    List dublinCore = new ArrayList<DCValue>();        
	        for(DCValue dcv:allDC) {
		        dublinCore.add(dcv);
	        }
	        Iterator it = dublinCore.iterator();
	  	    SearchResult rest = new SearchResult();
	        while(it.hasNext()) {
	        	DCValue dcv = (DCValue) it.next();
	        	System.out.println("dcvalue > to string    "+dcv.toString());
	        	if(dcv.element.equalsIgnoreCase("title")) {
	        		rest.setTitle(dcv.value);
	        		System.out.println("\n\nElement name ->"+dcv.element+" Element Value ->"+dcv.value);
	        	}
	        	if(dcv.element.equalsIgnoreCase("contributor")) {
	        		String str[]= {""};
	        		str[0]=dcv.value;
	        		rest.setAuthors(str);
	        		System.out.println("\n\nElement name ->"+dcv.element+" Element Value ->"+dcv.value);
	        	}
	        	if(dcv.element.equalsIgnoreCase("publisher")) {
	        		rest.setPublisher(dcv.value);
	        		System.out.println("\n\nElement name ->"+dcv.element+" Element Value ->"+dcv.value);
	        	}
	        	if(dcv.element.equalsIgnoreCase("description")) {
	        		if(dcv.qualifier != null){
	        			if(dcv.qualifier.equalsIgnoreCase("sponsorship")) {
	        				rest.setSponsor(dcv.value);
	    	        		System.out.println("\n\nElement name ->"+dcv.element+" Element Value ->"+dcv.value);
	        			}
	        		}
	        		
	        	}
	        	if(dcv.element.equalsIgnoreCase("description")) {
	        		if(dcv.qualifier == null){
	        			rest.setDescription(dcv.value);
  	        		System.out.println("\n\nElement name ->"+dcv.element+" Element Value ->"+dcv.value);
	        			/*if(dcv.qualifier.equalsIgnoreCase("provenance")) {
	        				rest.setDescription(dcv.value);
	    	        		System.out.println("\n\nElement name ->"+dcv.element+" Element Value ->"+dcv.value);
	        			}*/
	        		}
	        		
	        	}
	        	if(dcv.element.equalsIgnoreCase("identifier")) {
	        		if(dcv.qualifier != null){
	        			if(dcv.qualifier.equalsIgnoreCase("uri")) {
	    	        		String str[] = dcv.value.split("/");
	    	        		rest.setUrl(str[3]+"/"+str[4]);
	    	        		System.out.println("\n\nElement name ->"+dcv.element+" Element Value ->"+dcv.value);
	        			}
	        		}
	        	}	   
	        }
	        return rest;
	}
}
