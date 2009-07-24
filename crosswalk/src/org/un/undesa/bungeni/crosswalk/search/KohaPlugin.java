package org.un.undesa.bungeni.crosswalk.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import java.util.*;

public class KohaPlugin implements PluginInterface {

	public SearchResults getDetails(SearchParameters sp) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public SearchResults search(SearchParameters query) throws Exception {
		
		String result = buildkstring(query);
		/*Principal p = request.getUserPrincipal();
		System.out.println("\nUsername: "+p.getName());*/

		SearchResults results = new SearchResults();
		query.setStartOffset((query.getStartOffset().equalsIgnoreCase("")==true?"1":query.getStartOffset()));
		System.out.println("The page size is: "+query.getStartOffset());
		results.setStartRecord((Integer.valueOf(query.getStartOffset())));
		results.setPageSize(20);
		
		// Create an instance of HttpClient.
	    HttpClient client = new HttpClient();

	    


	    String responsible = null;
	    	
	      List<SearchResult> res= new ArrayList<SearchResult>();
	    	  
	    	  System.out.println("\n\nSending requst");	    	  
	    	  
	    	  String rl = "version=1.1&operation=searchRetrieve&x-pquery="+result+"&startRecord="+((Integer.valueOf(query.getStartOffset())+Integer.valueOf(query.getRetrieveNumber()))+1)/*(((Integer.valueOf(query.getStartOffset()))*Integer.valueOf(query.getRetrieveNumber()))+1)*/+"&maximumRecords="+query.getRetrieveNumber();
	    	  System.out.println("\n\nrl = :"+rl);
	  		  byte[] responseBody = null;
	  		  
	  		HttpMethod methodz = new GetMethod(query.getUrl());
	  	    try {
	  	    	System.out.println("\n\nGotten method? :");
	  		    methodz.setQueryString(/*pairs*/URIUtil.encodeQuery(URIUtil.decode(rl)));
	  		    System.out.println("\n\nWith Query String: "+methodz.getURI());
	  		    
	  	    	//method = new GetMethod(/*url3*//*"http://localhost:8080/"*/url3);/*off lim*/
	  		    // Provide custom retry handler is necessary
	  	    	System.out.println("\n\nGotten method? :");
	  		    methodz.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	  		    		new DefaultHttpMethodRetryHandler(3, false));
	  		    //method.getParams().setParameter("ident", new Integer(3));
	  		    
	  		  System.out.println("\n\nGotten method? :");
	  	        // Execute the method.
	  	        int statusCode = client.executeMethod(methodz);

	  	        if (statusCode != HttpStatus.SC_OK) {
	  	          System.err.println("Method failed: " + methodz.getStatusLine());
	  	        }
	  	      System.out.println("\n\nGotten method? :");

	  	        // Read the response body.
	  	        responseBody = methodz.getResponseBody();

	  	        // Deal with the response.
	  	        // Use caution: ensure correct character encoding and is not binary data
	  	        responsible = new String(responseBody);
	  	        

	  	      } catch (HttpException e) {
	  	        System.err.println("Fatal protocol violation: " + e.getMessage());
	  	        e.printStackTrace();
	  	        responsible = null;
	  	      } catch (IOException e) {
	  	        System.err.println("Fatal transport error: " + e.getMessage());
	  	        e.printStackTrace();
	  	        responsible = null;
	  	      } finally {
	  	        // Release the connection.
	  	        methodz.releaseConnection();
	  	      }  

	  	      if(responsible == null) {
	  	    	  SearchResults sr = new SearchResults();
	  	    	  List<SearchResult> sr2 = new ArrayList<SearchResult>();
	  	    	  sr.setSearchResults(sr2);
	  	    	  sr.setStartRecord(0);
	  	    	  return sr;
	  	      }
	  	    SearchResult rest = new SearchResult();
	  	    InputStream bai = new ByteArrayInputStream(responseBody);
	  	    InputStream bai2 = new ByteArrayInputStream(responseBody);
		    
	  	    MarcReader reader = new MarcXmlReader(bai);
	  	    
	  	  System.out.println("\n\nPrinting responsible: \n\n\n\n\n"+responsible+"\n\n\n\n\n\n\n\n\n\n\n\n");
	/*  	  
	  	if (reader.hasNext()) {
	  		Record record = reader.next();
	  		System.out.println(record.toString());
	  		} else {
	  		System.err.println("Reader has no record.");
	  		}
	  	if (reader.hasNext()) {
	  		Record record = reader.next();
	  		System.out.println(record.toString());
	  		} else {
	  		System.err.println("Reader has no record.");
	  		}
	  	if (reader.hasNext()) {
	  		Record record = reader.next();
	  		System.out.println(record.toString());
	  		} else {
	  		System.err.println("Reader has no record.");
	  		}
*/
	  	  int workaround = 2;
	  	  
	  	  while (reader.hasNext()) {
	  		  /**
	  		   * It seems there is a bug in marc4j which renders each record twice from the marc xml input. As a temporary workaround,
	  		   * This module only works on modulo 2 records, thereby eliminating the duplicates
	  		   * @Alex
	  		   */
	  		  if(workaround % 2 == 0) { workaround++;reader.next();continue;}
              Record record = reader.next();
              System.out.println(record.toString());
              
              //Leader leader = record.getLeader();
              
              rest = new SearchResult();
              // get the first field occurence for a given tag
              try {
	              DataField title = (DataField) record.getVariableField("245");
	              System.out.println("\n\n\n\nTag gotten -> "+title.getTag()+"    "+workaround);
	              char ind1 = title.getIndicator1();
	              char ind2 = title.getIndicator2();
	              System.out.println("\nTag: " + title.getTag() + " Indicator 1: " + ind1 + " Indicator 2: " + ind2);
	
	              List<Subfield> subfields = title.getSubfields();
	              
	              //Replaced the Iterator with java5 generics; its cleaner, easier to understand & thread-safe
	              for(Subfield subfield: subfields) {
	            	  char code = subfield.getCode();
			          	String data = subfield.getData();
			
			          	System.out.println("Subfield code: " + code + " Data element: " + data);
			          	switch(code) {
			          		case 'a':
			          			rest.setTitle(StringEscapeUtils.escapeXml(data));
			          			//rest.setTitle("saghj");
			          			break;
			          		case 'b':
			          			
			          			break;
			          	}
	              }
              }catch(Exception e) {
            	  e.printStackTrace();
              }
              
              try {
	              DataField description = (DataField) record.getVariableField("500");
	              if(description!=null) {
	            	  List<Subfield> subfields2 = description.getSubfields();
	                  Iterator i2 = subfields2.iterator();
	                  while (i2.hasNext()) {
	                      Subfield subfield = (Subfield) i2.next();
	    		          	char code = subfield.getCode();
	    		          	String data = subfield.getData();
	    		
	    		          	System.out.println("Subfield code: " + code + " Data element: " + data);
	    		          	switch(code) {
	    		          		case 'a':
	    		          			rest.setDescription(StringEscapeUtils.escapeXml(data));
	    		          			//rest.setDescription("dfghj");
	    		          			break;
	    		          		case 'b':
	    		          			
	    		          			break;
	    		          	}
	                  }
	              }
              }catch(Exception e) {
            	  e.printStackTrace();
              }
              
              try {
	              DataField description = (DataField) record.getVariableField("999");
	              if(description!=null) {
	            	  List<Subfield> subfields2 = description.getSubfields();
	                  Iterator i2 = subfields2.iterator();
	                  while (i2.hasNext()) {
	                      Subfield subfield = (Subfield) i2.next();
	    		          	char code = subfield.getCode();
	    		          	String data = subfield.getData();
	    		
	    		          	System.out.println("Subfield code: " + code + " Data element: " + data);
	    		          	switch(code) {
	    		          		case 'c':
	    		          			rest.setUrl(data);//.setDescription(StringEscapeUtils.escapeXml(data));
	    		          			//rest.setDescription("dfghj");
	    		          			break;
	    		          		case 'd':
	    		          			rest.setUrl(data);
	    		          			break;
	    		          	}
	    		          	System.out.println("\n\nkohaid:" + rest.getUrl());
	                  }
	              }
              }catch(Exception e) {
            	  e.printStackTrace();
              }
              
              
              if(rest.getDescription() == null)rest.setDescription(rest.getTitle());
              rest.setBitstreams("NONE");
              rest.setCollection("koha");
              rest.setLanguage("en");
              
              
              try {
	              DataField publisher = (DataField) record.getVariableField("260");	                         
	              if(publisher!=null) {
	            	  List subfields3 = publisher.getSubfields();
	                  Iterator i3 = subfields3.iterator();
	                  while (i3.hasNext()) {
	                      Subfield subfield = (Subfield) i3.next();
	    		          	char code = subfield.getCode();
	    		          	String data = subfield.getData();
	    		
	    		          	System.out.println("Subfield code: " + code + " Data element: " + data);
	    		          	switch(code) {
	    		          		case 'a':		          			
	    		          			break;
	    		          		case 'b':
	    		          			rest.setPublisher(StringEscapeUtils.escapeXml(data));
	    		          			break;
	    		          		case 'c':
	    		          			System.out.println("\ndatefield"+data);
	    		          			String date = data;
	    		          			
	    		          			System.out.println("\n\nlastindexoof:"+date.lastIndexOf(".")+":");
	    		          			if(date.lastIndexOf(".") != -1)
	    	            			date = date.substring(0, date.lastIndexOf("."));
	    	            			
	    	            			System.out.println("\n\ndaaate:"+date);
	    	            			Calendar cdr = Calendar.getInstance();
	    	            			cdr.set(Integer.valueOf(date), 1, 1);
	    		          			rest.setIssue_date(cdr.getTime());
	    		          			//Calendar.getInstance().set(Integer.valueOf(date), 1, 1);
	    		          			
	    		          			System.out.println("\ndate:"+rest.getIssue_date()+"::"+date);
	    		          			break;
	    		          	}
	                  }
	              }
              }catch(Exception e) {
            	  e.printStackTrace();
              }
              		
              try{
            	  ControlField issue_date= (ControlField) record.getVariableField("005");
            		if(issue_date !=null){
            			String date = issue_date.getData();
            			date = date.substring(0, date.lastIndexOf(".")													);
            			System.out.println("\nqwerty:"+issue_date.getData()+"::"+issue_date.toString()+"::"+date);
            			//rest.setIssue_date(new Date(date));
            		}
              }catch(Exception ex){
            	  ex.printStackTrace();
              }
              
              try {
	              DataField authors = (DataField) record.getVariableField("100");
	              List subfields4 = authors.getSubfields();
	              Iterator i4 = subfields4.iterator();
	              while (i4.hasNext()) {
	                  Subfield subfield = (Subfield) i4.next();
			          	char code = subfield.getCode();
			          	String data = subfield.getData();
			
			          	System.out.println("Subfield code: " + code + " Data element: " + data);
			          	switch(code) {
			          		case 'a':
			          			rest.setAuthor(data);
			          			break;
			          		case 'b':		          			
			          			break;
			          	}
	              }
              }catch(Exception ex) {
            	  ex.printStackTrace();
              }
              
              if(rest.getAuthor()==null){
            	  try {
    	              DataField authors = (DataField) record.getVariableField("110");
    	              List subfields4 = authors.getSubfields();
    	              Iterator i4 = subfields4.iterator();
    	              while (i4.hasNext()) {
    	                  Subfield subfield = (Subfield) i4.next();
    			          	char code = subfield.getCode();
    			          	String data = subfield.getData();
    			
    			          	System.out.println("Subfield code: " + code + " Data element: " + data);
    			          	switch(code) {
    			          		case 'a':
    			          			rest.setAuthor(data);
    			          			break;
    			          		case 'b':		          			
    			          			break;
    			          	}
    	              }
                  }catch(Exception ex) {
                	  ex.printStackTrace();
                  }
              }
              //rest.setAuthor(" ");
              System.out.println("\n\n\n\n\n > adding rest" +rest.getDescription());
              //if(rest.getUrl()==null)rest.setUrl("null");
              res.add(rest);
              workaround++;

         } 
	  	  
	  	  
	  	  System.out.println("\n\nReader finished gettingtotal ");
	  	  SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
	  	  String maxRes= "";
	  	  try {
		  	  org.jdom.Document jdomDocument = saxBuilder.build(bai2);
		  	  org.jdom.Element node = (org.jdom.Element) XPath.selectSingleNode(jdomDocument, "//zs:searchRetrieveResponse/zs:numberOfRecords");
		  	  System.out.println("sax text:"+node.getText()+"    value:"+node.getValue());
		  	  maxRes = node.getText();
		  	  System.out.println("\n\nafter gettext");
	  	  }catch(Exception ex) {
	  		  ex.printStackTrace();
	  	  }
	  	  System.out.println("\n\nafter try");
	  	  /**
			 * TODO: Figure out max records
			 */
	  	  System.out.println("maxresults:"+maxRes+" and:"+query.getRetrieveNumber());
	  	  try {
	  		results.setMaxPages(Integer.valueOf(maxRes)/Integer.valueOf(query.getRetrieveNumber()));
	  	  }catch(Exception ex) {
	  		  System.out.println("\n\nerror in setmaxres");
	  		  results.setMaxPages(0);
	  		  //ex.printStackTrace();
	  	  }
	  	  /**/results.setMaxPages(0);
	  	  System.out.println("\n\nafter all tries");
	    
	    try{
	    	results.setMaxResults(Integer.valueOf(maxRes));
	    }catch(Exception ex) {
	    	results.setMaxResults(0);
	    }
	    
	      results.setSearchResults(res);
	      return results;
	}
	
	private String buildkstring(SearchParameters sp) {
		
		String build,build2;
		build=build2="";
		//System.out.println("About to trin 7:"+query7);
		if(sp.getKeyword()!=null) {//Author
			System.out.println("\n\n\n\n\n\nreached keyword :"+sp.getKeyword());
			build = "@attr 1=1016 "+sp.getKeyword().trim()+" ";//+build+" ";
			
			//build+="@attr 1=1003 \""+query1+"\"";//@attr 1=author @attr 2=102
		}else {
			System.out.println("\n\n\n\n\n\nNo keys");
		}
		
		if(sp.getAuthors()!=null) {//Author
			System.out.println("\n\n\n\n\n\n\nreached author");
			System.out.println("\nAuthor:"+sp.getAuthors());
			StringTokenizer st = new StringTokenizer(build.equalsIgnoreCase("")? sp.getAuthors():build,">");
			int mod = 0;
			System.out.println("\n\ntokens counted: "+st.countTokens());
			//StringTokenizer st = new StringTokenizer(p);
            while (st.hasMoreTokens()/* && st.countTokens() !=1*/) {
                //String sourceName = st.nextToken();
            	//mod++;
            	String token = st.nextToken();
            	System.out.println("\n\ntokenn :="+token);
            	if(mod != 0) {
            		build2 = build2+"@or ";
            		build = "@attr 1=1003 "+token.trim()+" "+build+" ";
            	}else {
            		build = "@attr 1=1003 "+token.trim()+" "+build+" ";
            	}
            	mod++;
            	
                // Use context class loader, falling back to Class.forName
                // if and only if this fails...
                
            }
            System.out.println("\n\n\n\n\n\nafter 1st loop:"+build2+build);
            StringTokenizer st2 = new StringTokenizer(build==null? sp.getAuthors():build,"~");
			int mod2 = 0;
			//StringTokenizer st = new StringTokenizer(p);
			while (st2.hasMoreTokens()/* && st.countTokens() !=1*/) {
                //String sourceName = st.nextToken();
            	//mod++;
            	String token = st2.nextToken();
            	String token2="";
            	try {
            		token2 = st2.nextToken();
            	}catch(Exception ex) {
            		String tmp=token;
            		token=build;
            		token2=tmp;
            		ex.printStackTrace();
            	}
            	
            	String bui="";
            	System.out.println("\n\ntokenn1 :="+token);
            	System.out.println("\n\ntokenn2 :="+token2);
            	if(mod2 != 0) {
            		
            		bui=" @attr 1=1003 "+token2;
            		build=bui+" "+token;
            		//build = "@attr 1=1003 "+token.trim()+" "+token2+" ";
            	}else {
            		System.out.println("\nin else");
            		build2 = build2+" @and";
            		bui=" @attr 1=1003 "+token2;
            		build=bui+" "+token;
            		//build = "@attr 1=1003 "+token.trim()+" "+token2+" ";
            	}
            	mod2++;
            	
            	System.out.println("\nbldis:"+build);
            	
                // Use context class loader, falling back to Class.forName
                // if and only if this fails...
                
            }
			System.out.println("\n\n\n\n\n\nafter 2nd loop:"+build2+build);
            
            
			
			
			//build+="@attr 1=1003 \""+query1+"\"";//@attr 1=author @attr 2=102
		}else {
			System.out.println("\n\n\nNo authors");
		}
		
		
		/*if(query3 != null) {
			build+="@attr 1=1 @attr 4=2 @attr 3=3 "+query1;
		}
		if(query4 != null) {//title
			build+="@attr 1=4 "+query1;
		}
		if(query7 != null) {//title
			build+="\""+query7+"\"";
		}*/
		String finals = build2+build;
		String result=finals;
		System.out.println("\n\nfinals"+finals);
		
		
		/**
		 * TODO: be sure to replace with non-deprecated value
		 * */
		return finals;//java.net.URLEncoder.encode(/*build*/finals);
	}

}
