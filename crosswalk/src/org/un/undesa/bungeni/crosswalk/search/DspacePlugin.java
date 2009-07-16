package org.un.undesa.bungeni.crosswalk.search;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.springframework.ui.ModelMap;

import com.thoughtworks.xstream.XStream;
import org.dspace.sort.SortOption;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.abdera.xpath.XPath;
import org.apache.abdera.*;
import org.apache.abdera.parser.*;
import org.apache.abdera.ext.opensearch.IntegerElement;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.*;
import org.apache.commons.lang.StringEscapeUtils;

public class DspacePlugin implements PluginInterface {

	public SearchResults getDetails(SearchParameters sp) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("\n\n\nReached dsearch plugin getDetails");	  
		
		
		
		
		
		
		GetMethod methodz = new GetMethod(sp.getUrl()+"/showList");
	    String uri = "handle="+sp.getIdentifier();
		//System.out.println("\nx-url is "+uri);		
	    String responsible = null;
	    
	    try {  		    
  		    // Provide custom retry handler is necessary
  		    methodz.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
  		    		new DefaultHttpMethodRetryHandler(3, false));
  		    //method.getParams().setParameter("ident", new Integer(3));
  		    
  		    // Create an instance of HttpClient.
  		    HttpClient client = new HttpClient();
  		    
  		    //HttpMethod methodz = new GetMethod(uri);
  		    methodz.setQueryString(URIUtil.encodeQuery(uri));
  		    System.out.println("\n\nWith Query String: "+methodz.getURI());
  		    
  		    System.out.println("\n\nabout 2 execute method");
  	        // Execute the method.
  	        int statusCode = client.executeMethod(methodz);
  	        System.out.println("\n\nexecuted method");

  	        if (statusCode != HttpStatus.SC_OK) {
  	          System.err.println("Method failed: " + methodz.getStatusLine());
  	        }

  	        // Read the response body.
  	        byte[] responseBody = methodz.getResponseBody();

  	        // Deal with the response.
  	        // Use caution: ensure correct character encoding and is not binary data
  	        responsible = new String(responseBody);
  	        //System.out.println(responsible);

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
  	    	  //List<SearchResult> sr2 = new ArrayList<SearchResult>();
  	    	  //sr.setSearchResults(sr2);
  	    	  sr.setStartRecord(0);
  	    	  return sr;
  	      }  

  	      XStream xstream = new XStream();
  	      SearchResults results = (SearchResults)xstream.fromXML(responsible);
  	      //if(results.getStartRecord()==0) {results.setStartRecord(1);}
  	      //results.setMaxPages(50);//TODO: change to actual
  	      System.out.println("\n\nmaxresultsz:"+results.getMaxResults());
  	      System.err.println(responsible);
  	      
  	      
  	      return results;
	}

	public SearchResults search(SearchParameters query) throws Exception {
		System.out.println("\n\n\nReached dsearch plugin");	
		
		
		SearchResults results = null;

		/*
		//or
		// Select the id of the document
		XPath xpath = abdera.getXPath();
		String id2 = xpath.valueOf("/a:feed/a:id", doc);

		// Select all entries from the document
		List entries2 = xpath.valueOf("//a:entry", doc);
		for (Iterator i = entries2.iterator(); i.hasNext();) {
		  Entry entry = (Entry)i.next();
		  //...
		}

		// Determine if a feed contains a specific extension
		boolean hasFoo = xpath.booleanValueOf("//x:foo", doc);

		// The XPath support works on any element in the FOM
		Entry entry = (Entry)xpath.selectSingleNode("//a:entry", doc);
		String id3 = xpath.valueOf("a:id", entry);*/
		
		
		String uri = "";
		String kw = query.getKeyword();
		try {
			kw = kw.trim();
		}catch(Exception e) {}
		try {
			if(kw.equalsIgnoreCase("")) {
				kw=null;
			}
		}catch(Exception e) {
			kw=null;
			e.printStackTrace();
		}
		if(query.getLocation()==null) {query.setLocation("");}
		if(query.getLocation() == null) { System.out.println("loc null");}else {  System.out.println("loc not null: "+ query.getLocation());}
	    if(kw==null && query.getAuthors() ==null && query.getIssue_date_string()==null && query.getTitle()==null
	    		&& query.getDescription()==null && query.getLocation() != null) {

	    	String offset = query.getStartOffset().equalsIgnoreCase("")==true?"0":query.getStartOffset();
	    	if(query.getLocation().startsWith("*")) {
	    		String location = query.getLocation().substring(1);
	    		uri="parliament="+query.getYear()+"&range="+location+" TO "+location+"&start="+offset;
			    System.out.println("\nx-url is "+uri + " > bungeni-search");
			    results = opensearch(query.getUrl(), uri);
	    	}else if (query.getYear().startsWith("@")){
	    		uri="parliament="+query.getLocation()+"&start="+offset;
			    System.out.println("\nx-url is "+uri + " > @bungeni-search");
			    results = opensearch(query.getUrl(), uri);
	    	}else {
	    		//build the query to dspace
		    	
			    uri = "handle="+query.getLocation()+"&start="+offset;
			    System.out.println("\nx-url is "+uri+" > listItems");
			    results = collections(query.getUrl(), uri);
	    	}
	    	
	    }else {
	    	//build the query to dspace
	    	System.out.println("\n\n\n\nrange="+query.getIssue_date_string());
		    uri = ((query.getAuthors()==null?null:query.getAuthors().trim()) == null ? "field1=author&query1=&" : "field1=author&query1="+query.getAuthors().trim().replaceAll("!", "NOT").replaceAll(">", "OR").replaceAll("~", "AND")+"&")
		    +((query.getAbstract()==null?null:query.getAbstract().trim()) == null ? "field2=keyword&query2=&" : "field2=keyword&query2="+query.getAbstract().trim().replaceAll("!", "NOT").replaceAll(">", "OR").replaceAll("~", "AND")+"&")+((query.getTitle()==null?null:query.getTitle().trim()) == null ? "field3=title&query3=&" : "field3=title&query3="+query.getTitle().trim().replaceAll("!", "NOT").replaceAll(">", "OR").replaceAll("~", "AND")+"&")+
		    ((query.getKeyword()==null?null:query.getKeyword().trim()) == null ? "field4=ANY&query4=&" : "field4=ANY&query4="+query.getKeyword().trim().replaceAll("!", "NOT").replaceAll(">", "OR").replaceAll("~", "AND")+"&")+"pagesize="+query.getRetrieveNumber()+"&start="+(query.getStartOffset().equalsIgnoreCase("")==true?"0":(Integer.valueOf(query.getStartOffset())/*-1*/))+"&sort_by="+query.getSortKey()+"&order="+query.getSortType()+"&num_search_field=4";
		    if(query.getIssue_date_string()!=null) {
		    	if(!query.getIssue_date_string().equalsIgnoreCase("")) {
		    		uri = uri +"&range="+query.getIssue_date_string();
		    	}		    	
		    }else if(query.getYear()!=null) {
		    	if(!query.getYear().equalsIgnoreCase("")) {
		    		uri = uri+"&parliament="+query.getYear();
		    	}
		    }
		    if(query.getLocation()!=null) {
		    	if(query.getLocation().startsWith("*")) {
			    	String location = query.getLocation().substring(1);
			    	uri=uri+"&range="+location+" TO "+location;
			    }else {
			    	uri=uri+"&loc="+query.getLocation();
			    }
		    }		    
		    
		    System.out.println("\nx-url is "+uri + " > bungeni-search");
		    results = opensearch(query.getUrl(), uri);
		    
	    }
	    
	    //if(results.getStartRecord()==0) {results.setStartRecord(1);}
  	      //results.setMaxPages(50);//TODO: change to actual
  	      System.out.println("\n\nmaxresults:"+results.getMaxResults());
  	      if(results.getSearchResults()==null)System.out.println("\n\n\nsearchresultsnull");
  	      
  	      
  	      return results;
	}
	
	public SearchResults opensearch(String url, String params) throws Exception{
		SearchResults results = new SearchResults();
		
		Abdera abdera = new Abdera();
		Parser parser = abdera.getParser();
		String urii = url+"/bungeni-opensearch?"+params;//"http://localhost:8080/crosswalk/bungeni-opensearch?pagesize=20&start=0&field1=ANY&query1=report";//new URI("http://example.org/feed.xml");
		urii = urii.replace(" ", "%20");
		URI urii2 = new URI(urii);//IRI
		InputStream in = urii2.toURL().openStream();
		ParserOptions options = parser.getDefaultParserOptions();
		options.setCharset("utf-8");
		//.. set other parser options
		Document<Feed> doc = parser.parse(in, urii, options);
		Feed feed = doc.getRoot();
		/*IRI id = feed.getId();
		Text.Type titleType = feed.getTitleType();
		String title = feed.getTitle();*/
		
		System.out.println(feed.getExtensions() + "zxzxzx"+ feed.getExtensions(OpenSearchConstants.ITEMS_PER_PAGE));
		List<Element> el = feed.getExtensions(OpenSearchConstants.ITEMS_PER_PAGE);
		String ipp = el.get(0).getText();
		System.out.println("\n"+ipp);
		
		results.setStartRecord(Integer.valueOf((feed.getExtensions(OpenSearchConstants.START_INDEX).get(0).getText())));
		results.setMaxResults(Integer.valueOf((feed.getExtensions(OpenSearchConstants.TOTAL_RESULTS).get(0).getText())));
		results.setPageSize(Integer.valueOf(ipp));
		results.setSearchDescription(feed.getTitle());
		
		List<SearchResult> reslist = new ArrayList<SearchResult>();
		List<Entry> entries = feed.getEntries();
		for (Entry entry : entries) {
			SearchResult sr = new SearchResult();
		  //IRI entryId = entry.getId();
		  //Text.Type entryTitleType = entry.getTitleType();
		  //String entryTitle = entry.getTitle();
		  try {
			  //System.out.println("\n\n\nentryTitle: "+ entryTitle);  
			  
			  //sr.setBitstream()
			  
			  System.out.println("\n\n\n\nzxcvbnm,String: "+ entry.getSummary());
			  sr.setDescription(entry.getSummary());
			  sr.setIssue_date(entry.getPublished());
			  sr.setUrl(entry.getId().toString());
			  sr.setTitle(entry.getTitle());
			  sr.setLanguage(entry.getLanguage());
			  
			  XPath xpath = abdera.getXPath();
			  /*String id3 = xpath.valueOf("a:content/b:div/b:div/b:dl/b:h2", entry);
			  System.out.println("\n\n\n\n\ncollection is: "+id3+":zz");
			  
			  String id4 = xpath.valueOf("a:content/a:div/a:div/a:dl/a:h2", entry);
			  System.out.println("\n\n\n\n\ncollection is: "+id4+":zz2");*/
			  
			  String id5 = xpath.valueOf("a:content", entry);
			  System.out.println("\n\n\n\n\ncollection is: "+id5+":zz3"+entry.getContent());
			  
			  String e = entry.getContent();
			  e = e.substring(e.indexOf("<h2>")+4, e.indexOf("</h2>"));
			  System.out.println("\n\nxpath:"+e);

			  sr.setCollection(e);
			  
			  e = entry.getContent();
			  e = e.substring(e.indexOf("<h3>")+4, e.indexOf("</h3>"));
			  System.out.println("\n\nxpath:"+e);
			  e=e.replace("href=\"/crosswalk", "href=\"/jspui");
			  sr.setBitstreams(e);
			  
			  sr.setAuthor(entry.getAuthor().getName());
		  }catch(Exception ex) {
			  ex.printStackTrace();
		  }		
		  reslist.add(sr);
		}
		
		results.setSearchResults(reslist);
		return results;
	}
	
	public SearchResults collections(String url, String params){
		SearchResults results = new SearchResults();
		GetMethod methodz = null;
		
		methodz = new GetMethod(url+"/listItems");
		String responsible = null;
	    
	    try {  		    
  		    // Provide custom retry handler is necessary
  		    methodz.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
  		    		new DefaultHttpMethodRetryHandler(3, false));
  		    //method.getParams().setParameter("ident", new Integer(3));
  		    
  		    // Create an instance of HttpClient.
  		    HttpClient client = new HttpClient();
  		    
  		    //HttpMethod methodz = new GetMethod(uri);
  		    methodz.setQueryString(URIUtil.encodeQuery(params));
  		    System.out.println("\n\nWith Query String: "+methodz.getURI());
  		    
  		    System.out.println("\n\nabout 2 execute method");
  	        // Execute the method.
  	        int statusCode = client.executeMethod(methodz);
  	        System.out.println("\n\nexecuted method");

  	        if (statusCode != HttpStatus.SC_OK) {
  	          System.err.println("Method failed: " + methodz.getStatusLine());
  	        }

  	        // Read the response body.
  	        byte[] responseBody = methodz.getResponseBody();

  	        // Deal with the response.
  	        // Use caution: ensure correct character encoding and is not binary data
  	        responsible = new String(responseBody);
  	        //System.out.println(responsible);

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
  	    	  //List<SearchResult> sr2 = new ArrayList<SearchResult>();
  	    	  //sr.setSearchResults(sr2);
  	    	  sr.setStartRecord(0);
  	    	  return sr;
  	      }

  	      XStream xstream = new XStream();
  	      results = (SearchResults)xstream.fromXML(responsible);  	      
  	      
  	      return results;
	}

}
