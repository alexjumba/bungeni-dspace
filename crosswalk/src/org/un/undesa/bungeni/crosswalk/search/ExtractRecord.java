package org.un.undesa.bungeni.crosswalk.search;

import java.io.IOException;
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
import org.dspace.content.Collection;

import com.thoughtworks.xstream.*;
import java.io.*;
import org.dspace.search.*;

import java.util.*;
import org.dspace.core.Constants;
import org.dspace.handle.HandleManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.dspace.app.webui.util.UIUtil;

public class ExtractRecord extends HttpServlet
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
    	System.out.println("\n\n\n\n\n\nResults count: "+qrs.getHitCount());    	
    	
    	DspaceResults dsr = new DspaceResults();
    	dsr.setResults(qrs);
    	List<Item> items = new ArrayList<Item>();
    	//List<Item> items2 = new ArrayList<Item>();
    	// now instantiate the results and put them in their buckets
    	System.out.println("\n\n\n\n\n\nResults hit count size: "+qrs.getHitHandles().size());
    	
    	for (int i = 0; i < qrs.getHitTypes().size(); i++)
        {
            Integer myId    = (Integer) qrs.getHitIds().get(i);
            String myHandle = (String) qrs.getHitHandles().get(i);
            Integer myType  = (Integer) qrs.getHitTypes().get(i);
            System.out.println("\n\nxx:"+myId+":"+myHandle+":"+myType);
            
            
         // add the handle to the appropriate lists
            try{
            	switch (myType.intValue())
                {
                case Constants.ITEM:
                	Item it;
                    if (myId != null)
                    {
                        /*resultsItems[itemCount]*/it = Item.find(context, myId);
                    	System.out.println("\n\nmyid not null");
                    }
                    else
                    {
                    	System.out.println("\n\nmyid null");
                        /*resultsItems[itemCount]*/it = (Item)HandleManager.resolveToObject(context, myHandle);
                    }

                    if (it == null)
                    {
                        throw new SQLException("Query \""
                                + "\" returned unresolvable item");
                    }
                    break;
                }
            }catch(Exception ex){
            	ex.printStackTrace();
            }
            
        }
    	XStream xstreamv = new XStream();
    	System.out.println("\n\n dsr ->"+xstreamv.toXML(qrs.getHitIds()));
    	
    	@SuppressWarnings("unchecked") // This cast is correct
    	java.util.List<Integer> handlez = qrs.getHitIds();
    	System.out.println("\n\n{[]}"+qrs.getHitIds());
    	try{
    		for (int handle : handlez)
	        {
    			System.out.println("\n\n{}"+handle);
	        }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
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
	            		
	                    items.add(it);
	                    
	            		
	            		break;
	            }
	            
	        }
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}
        //items2.add((Item) new Object());
        dsr.setItem(items.isEmpty() ? null : items);
    	
        
        XStream xstream = new XStream();
    	//System.out.println("\n\n dsr ->"+xstream.toXML(dsr));
    	
        SearchResults sr = searchResults(dsr, request);
    	
    	
    	
        String xml = xstream.toXML(sr == null ? new SearchResults() : sr);
    	
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
        
        
        /*int cache=bif.read(b);
        int tot=cache;
        try {
	        while (cache != -1)
	        {
	        	System.out.println("b is>: "+b+" cache is >:"+cache);
	                stream.write(b,0,cache);
	                //out.write(b,0,cache);
	                cache = bif.read(); //b,0,1024
	                tot+=cache;
	        }
        }catch(Exception ex) {
        	ex.printStackTrace();
        }
        if(bif!=null)bif.close();
        if(stream!=null) stream.close();*/
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
		try {
			qa.setPageSize(Integer.parseInt(hsr.getParameter("pagesize")));
		}catch(Exception ex) {
			ex.printStackTrace();
			qa.setPageSize(0);
		}
		try {
			qa.setStart(Integer.parseInt(hsr.getParameter("start")));
		}catch(Exception ex) {
			ex.printStackTrace();
			qa.setStart(0);
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
    	    }
    	    if (SortOption.ASCENDING.equalsIgnoreCase(order))
    	    {
    	        qa.setSortOrder(SortOption.ASCENDING);
    	        System.out.println("\n\nascending");
    	    }
    	    else
    	    {
    	        qa.setSortOrder(SortOption.DESCENDING);
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
		System.out.println("\n\nQuery String"+querystring);
		
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
		QueryArgs qat = new QueryArgs();
		qat.setQuery(/*query*/querystring);
		qat.setPageSize(2000);
		qat.setStart(0);
		qat.setSortOrder(SortOption.DESCENDING);
		qrst = DSQuery.doQuery(ctx, qat);
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
				qrs = DSQuery.doQuery(ctx, qa, cll);
			}catch(Exception ex) {
				ex.printStackTrace();
				try {
					comm = (Community)HandleManager.resolveToObject(getContext(hsr), loc);
					qrs = DSQuery.doQuery(ctx, qa, comm);
				}catch(Exception exx) {
					exx.printStackTrace();
				}
				//just in case
				qrs = DSQuery.doQuery(ctx, qa);
			}
		}else {
			qrs = DSQuery.doQuery(ctx, qa);
		}
		System.out.println("\n\nEventual query is:"+qa.getQuery());
	
		
		
		
		return qrst;
	}
	
	private SearchResults searchResults(DspaceResults dspaceResults, HttpServletRequest request) {
		System.out.println("\n\nreached search for bungeni");
		SearchResults results = new SearchResults();
	      results.setMaxResults(dspaceResults.getResults().getHitCount());
	      results.setMaxPages(dspaceResults.getResults().getHitCount());//TODO: change to actual
	      results.setPageSize(dspaceResults.getResults().getPageSize());
	      results.setStartRecord(dspaceResults.getResults().getStart());
	      List<SearchResult> srs = new ArrayList<SearchResult>();
	      List<Item> itmlst = dspaceResults.getItem();
	      if(itmlst == null) {
	    	  System.out.println("Item list null. Must be no results");
	    	  results.setMaxPages(0);
	    	  results.setMaxResults(0);
	    	  results.setPageSize(0);
	    	  results.setStartRecord(0);
	    	  results.setSearchResults(null);
	    	  return results;
	      }
	      int counter = 1;
	      for(Item item: itmlst) {
	    	  System.out.println("\n\n\n\n\n\n\n\n\n\n\ncounted so far:"+ counter);
	    	  counter++;
	    	  
	    	  //SearchResult sr = new SearchResult();
	    	  //sr.setAuthor(item.getDC( "author", Item.ANY, Item.ANY ).toString());
	  	    	// Get all the DC
	    	  //DCValue[] dcvv = item.getDC("", "", Item.ANY);
	    	  //item.getMetadata(schema, element, qualifier, Item.ANY);	   
	    	  SearchResult rest = new SearchResult();
	    	  String col = "";
	    	  Collection[] cln = null;
	    	  try {
	    		  cln =item.getCollections();
	    	  }catch(Exception ex) {
	    		  
	    	  }
	    	  for(Collection colln:cln) {
	    		  col = colln.getMetadata("name");
	    	  }
	    	  rest.setCollection(col);
	    	  DCDate dd = null;
	    	  DCValue val = null;
	    	  DCValue[] dcvv = item.getMetadata("dc", "date", "issued", Item.ANY);
	    	  if(dcvv.length > 0) {
	    		  dd = new DCDate(dcvv[0].value);
	    		  rest.setIssue_date(dd.toDate());
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
	    		  rest.setAuthors(str);
	    	  }
	    	  dcvv = item.getMetadata("dc", "title", Item.ANY, Item.ANY);
	    	  if(dcvv.length > 0) {
	    		  rest.setTitle(dcvv[0].value);
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
	    		  rest.setDetails(str);
	    	  }
	    	  dcvv = item.getMetadata("dc", "identifier", "uri", Item.ANY);
	    	  if(dcvv.length > 0) {
	    		  String str[] = dcvv[0].value.split("/");
	        		rest.setUrl(str[3]+"/"+str[4]);
	    	  }
	    	  
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
		        			rest.setBitstream(ltm);
		        		
		          	}
	    	  }catch(Exception ex) {
	    		  ex.printStackTrace();	    		  
	    	  }
	    	  
	    	  
		        srs.add(rest);	        
	    	  
	      }
	      results.setSearchResults(srs);
	      return results;
	}
	
}
