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


public class CopyOfListAllItems extends HttpServlet {
	protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
		System.out.println("\n\nreached listAllItems");
		// This will map community IDs to arrays of collections
        Map colMap = new HashMap();

        // This will map communityIDs to arrays of sub-communities
        //Map commMap = new HashMap();

        Community[] communities = null;
        try {
        	System.out.println("\n\nin findall try");
        	communities = Community.findAllTop(getContext(request));
        	System.out.println("\n\nin findall try after");
        }catch(Exception ex) {
        	System.out.println("\n\nin findall catch");
        	ex.printStackTrace();
        }
           	
        //List<Community> comm = new ArrayList<Community>();
        List<List<TransferMap>> ccl = null;
        
        try {
        	ccl=collect(communities);
        }catch(Exception ex) {
        	ex.printStackTrace();
        }        
        
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
	
	private List<List<TransferMap>> collect(Community[] communities) throws Exception {      
		List<IterableMap> lim = new ArrayList<IterableMap>();
		List<List<TransferMap>> tmll = new ArrayList<List<TransferMap>>();
		
        for(Community cm : communities) {        	
			if(cm.getSubcommunities().length == 0){
				System.out.println("\n\n\n\nNo subcommunities");
				for(org.dspace.content.Collection cn2: cm.getCollections()) {					
					ItemIterator ii = cn2.getAllItems();
					while(ii.hasNext()){
						Item it = (Item) ii.next();
						List<TransferMap> tml = new ArrayList<TransferMap>();
						
						TransferMap tm = new TransferMap();
						IterableMap plugin = new HashedMap();
						plugin.put("communityName", cm.getMetadata("name"));
						tm.setKey("communityName");
						tm.setValue(cm.getMetadata("name"));
						tml.add(tm);
						plugin.put("subCollectionName", "");
						tm = new TransferMap();
						tm.setKey("subCollectionName");
						tm.setValue("");
						tml.add(tm);
						plugin.put("collectionName", cn2.getMetadata("name"));
						tm = new TransferMap();
						tm.setKey("collectionName");
						tm.setValue(cn2.getMetadata("name"));
						tml.add(tm);
			        	
						for(TransferMap im: selectMetadata(it)) {
							//MapIterator mit = im.mapIterator();
							//plugin.put(mit.next(), mit.getValue());
							tml.add(im);
							//System.out.println("\n\n\n\nIter v:"+mit.next()+"   :"+mit.getValue());
						}
						lim.add(plugin);
						tmll.add(tml);
					}
		    	 }
			}else{	
				for(org.dspace.content.Community cn: cm.getSubcommunities()) {
					for(org.dspace.content.Collection cn2: cn.getCollections()) {
				    	ItemIterator ii = cn2.getAllItems();
						while(ii.hasNext()){
							Item it = (Item) ii.next();
							List<TransferMap> tml = new ArrayList<TransferMap>();
							
							TransferMap tm = new TransferMap();
							IterableMap plugin = new HashedMap();
							plugin.put("communityName", cm.getMetadata("name"));
							tm.setKey("communityName");
							tm.setValue(cm.getMetadata("name"));
							tml.add(tm);
							plugin.put("subCollectionName", cn.getMetadata("name"));
							tm = new TransferMap();
							tm.setKey("subCollectionName");
							tm.setValue(cn.getMetadata("name"));
							tml.add(tm);
							plugin.put("collectionName", cn2.getMetadata("name"));
							tm = new TransferMap();
							tm.setKey("collectionName");
							tm.setValue(cn2.getMetadata("name"));
							tml.add(tm);
							
							for(TransferMap im: selectMetadata(it)) {
								//MapIterator mit = im.mapIterator();
								//plugin.put(mit.next(), mit.getValue());
								tml.add(im);
								//System.out.println("\n\n\n\nIter v:"+mit.next()+"   :"+mit.getValue());
							}
							lim.add(plugin);
							tmll.add(tml);
						}
		    	    }	
				}	        		
		    }	
	   }        
       //return lim;
        //return tml;
        return tmll;
	}
	
	private List<TransferMap> selectMetadata(Item item){
		//List<IterableMap> iil = new ArrayList<IterableMap>();
		List<TransferMap> ltm = new ArrayList<TransferMap>();
		String col = "";
  	  Collection[] cln = null;
  	  try {
  		  cln =item.getCollections();
  	  }catch(Exception ex) {
  		  
  	  }
  	  for(Collection colln:cln) {
  		  col = colln.getMetadata("name");
  	  }
  	TransferMap tm2 = new TransferMap();
	  tm2.setKey("collection");
	  tm2.setValue(col);
	  ltm.add(tm2);
	  
		System.out.println("\n\nat selectMetadata");
  	  DCDate dd = null;
  	  DCValue val = null;
  	  DCValue[] dcvv = item.getMetadata("dc", "date", "issued", Item.ANY);
  	  if(dcvv.length > 0) {  		  
  		  dd = new DCDate(dcvv[0].value);
  		  IterableMap ir = new HashedMap();
  		  TransferMap tm = new TransferMap();
  		  tm.setKey("issueDate");
  		  tm.setValue(dd.toDate());
  		  ltm.add(tm);
  		  ir.put("issueDate", dd.toDate());
  		  //iil.add(ir);
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
  		  IterableMap ir = new HashedMap();
		  ir.put("authors", string);
		  TransferMap tm = new TransferMap();
  		  tm.setKey("authors");
  		  tm.setValue(string);
  		  ltm.add(tm);
		  //iil.add(ir);
		  System.out.println("\n\n\n\nAuthors: "+string);
  	  }
  	  dcvv = item.getMetadata("dc", "title", Item.ANY, Item.ANY);
  	  if(dcvv.length > 0) {
  		  IterableMap ir = new HashedMap();
		  ir.put("title", dcvv[0].value);
		  TransferMap tm = new TransferMap();
  		  tm.setKey("title");
  		  tm.setValue(dcvv[0].value);
  		  ltm.add(tm);
		  //iil.add(ir);
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
		
  		  IterableMap ir = new HashedMap();
		  ir.put("description", string);
		  TransferMap tm = new TransferMap();
  		  tm.setKey("description");
  		  tm.setValue(string);
  		  ltm.add(tm);
		  //iil.add(ir);
  	  }
  	  dcvv = item.getMetadata("dc", "identifier", "uri", Item.ANY);
  	  if(dcvv.length > 0) {
  		  String str[] = dcvv[0].value.split("/");
      		IterableMap ir = new HashedMap();
	  		  ir.put("url", str[3]+"/"+str[4]);
	  		TransferMap tm = new TransferMap();
	  		  tm.setKey("url");
	  		  tm.setValue(str[3]+"/"+str[4]);
	  		  ltm.add(tm);
	  		  //iil.add(ir);
  	  }
		
		//return iil;
  	  return ltm;
	}
}
