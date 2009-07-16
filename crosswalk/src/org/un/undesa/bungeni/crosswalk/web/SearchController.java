package org.un.undesa.bungeni.crosswalk.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.Principal;
import org.un.undesa.bungeni.crosswalk.search.*;
import org.w3c.dom.DOMImplementationSource;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dspace.content.Community;
import org.dspace.content.DCValue;
import org.dspace.content.Item;

import org.springframework.context.*;
import org.springframework.core.io.*;
import com.thoughtworks.xstream.XStream;

import flexjson.*;

import java.io.*;

import org.marc4j.MarcXmlReader;

import org.marc4j.marc.Record;
import org.marc4j.MarcReader;
import org.marc4j.marc.*;
import org.dspace.content.*;
import org.dspace.search.*;
import org.apache.commons.lang.*;
import org.springframework.web.bind.annotation.RequestMethod; 

import org.jdom.xpath.*;
import org.jdom.input.*;
import java.net.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.util.*;
import org.apache.commons.collections.*;
import org.apache.commons.collections.map.*;
import java.util.*;

/**
 * Annotation-driven <em>MultiActionController</em> that handles all non-form
 * URL's.
 *
 * Temporary measure as code is made RESTful
 * @author Alex
 */
@Controller
public class SearchController implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;
	private String configLocation = "classpath:plugins.xml";
	private String repositoriesLocation = "classpath:repositories.xml";
	private String reportsLocation = "classpath:reports.xml";
	private String languagesLocation = "classpath:languages.xml";
	private String defaultHome = "";
	
	/**
	 * function to return the union of two SearchResults sets
	 * @param sr1	The first set
	 * @param sr2	The second set
	 * @return
	 */
	private SearchResults unify(SearchResults sr1, SearchResults sr2) {
		if(sr1==null && sr2==null) {
			System.out.println("\n\nsr1&2=null");
			SearchResults localResults = new SearchResults();
			localResults.setMaxPages(0);
			   localResults.setMaxResults(0);
			   localResults.setPageSize(30);
			   localResults.setStartRecord(0);
			return localResults;
		}else if(sr1==null) {
			System.out.println("\n\nsr1=null");
			return sr2;
		}else if(sr2==null) {
			System.out.println("\n\nsr2=null");
			return sr1;
		}else {
			System.out.println("\n\nAbout to unionize collections");
			List<SearchResult> result = null;
			if(sr1.getSearchResults()==null) {
				System.out.println("\n\nsr1 SearchResults null");
				result = sr2.getSearchResults();
			}else if(sr2.getSearchResults()==null) {
				System.out.println("\n\nsr2 SearchResults null");
				result = sr1.getSearchResults();
			}else {
				try {
					System.out.println("\n\nCollectUtils");
					result= (List<SearchResult>) CollectionUtils.union(sr1.getSearchResults(), sr2.getSearchResults());
					Collections.sort(result, new Comparate());
				}catch(Exception ex) {
					System.out.println("\n\nException @ unify");
					ex.printStackTrace();
					//return null;
				}
			}
			
			System.out.println("\n\nafter catcher");
			SearchResults sr = new SearchResults();
			sr.setMaxPages(sr2.getMaxPages());
			sr.setMaxResults(sr1.getMaxResults()+sr2.getMaxResults());
			sr.setPageSize(sr2.getPageSize());
			sr.setSearchResults(result==null? /*new ArrayList<SearchResult>()*/null:result);
			sr.setStartRecord(sr2.getStartRecord()==0?/*1*/0:sr2.getStartRecord());
			return sr;
		}
	}
	
	public class Comparate implements Comparator<SearchResult> {
		   public int compare(SearchResult x, SearchResult y) {
		       // cast a and b to whatever object is in your ArrayList
		       //SearchResult x = (SearchResult) a;
		       //SearchResult y = (SearchResult) b;
		       Date datea, dateb;
		       datea = x.getIssue_date();
		       dateb = y.getIssue_date();
		       return datea.compareTo(dateb);
		       // now determine which if x > y return 1  x == y return 0  x < y return -1
		       //return -1;// or 0 or +1;
		   }
		}

	
	/**
	 * function to aggregate all the search parameters into an object for easy exchange
	 * @param query1
	 * @param query2
	 * @param query3
	 * @param query4
	 * @param query5
	 * @param query6
	 * @param query7
	 * @param start
	 * @param pagesize
	 * @param page
	 * @param rows
	 * @param soridx
	 * @param sorttyp
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private SearchParameters injectParameters(String query1, String query2, String query3, String query4, String dop, String query6, String query7,  String start,  String pagesize,  String page, String rows,  String soridx, String sorttyp, String loc, String url, String year  ) throws Exception{ 
		SearchParameters sp = new SearchParameters();
		System.out.println("\nAuthorsE: "+query1);
		sp.setAuthors(query1);
		sp.setSubject(query3);
		sp.setTitle(query4);
		sp.setAbstract(query2);
		sp.setKeyword(query7);
		sp.setIssue_date_string(dop);
		sp.setYear(year);
		try {
			if(page==null) {
				page="0";
			}
		}catch(Exception e) {
			e.printStackTrace();
		}		
		sp.setStartOffset(page);
		
		try {
			if(rows==null) {
				rows="20";
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			sp.setIssue_date(new Date(query7));
		}catch(Exception ex) {}
		sp.setRetrieveNumber(rows);
		sp.setSortKey(soridx);
		sp.setSortType(sorttyp);
		sp.setLocation(loc);
		//for now
		//sp.setUrl("http://localhost:8080/jspui/bungeni-search");
		//TODO: Plus others as we go on
		return sp;
	}
	
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader=resourceLoader;
	}
	
	@RequestMapping(value="/collection.do")
	public ModelMap collection(HttpServletRequest request, @RequestParam(value = "url", required = false) String url) throws Exception{
		
		HttpClient client = new HttpClient();
	    GetMethod method = new GetMethod(url+"/showCollections");
	    
	    String responsible = "";
	    try {
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

  	      System.out.println("\n\nb4 xstream");
	  	      XStream xstream = new XStream();
	  	      //CommunityCollection cc = (CommunityCollection)xstream.fromXML(responsible);
	  	    List<CommunityCollection> ccl = (List<CommunityCollection>) xstream.fromXML(responsible);
	  	    
	  	    //List<Community> comm = cc.getComm();
	  	      //Community[] comm = (Community[]) xstream.fromXML(responsible);
	  	    System.out.println("\n\nafter xstream");
	  	      //List<Community> community = new ArrayList<Community>();
	  	      /*for(Community cm : comm) {
	  	    	  community.add(cm);
	  	    	  for(org.dspace.content.Collection cn: cm.getCollections()) {
	  	    		  ItemIterator it = cn.getItems();
	  	    		  while(it.hasNext()) {
	  	    			  Item item = (Item) it.next();
	  	    		  }
	  	    	  }
	  	    	  
	  	      }*/
	  	      
	  	    Map<String,Object> mp = new HashMap<String,Object>();
		  	mp.put("results", /*community*//*comm*/ccl);	  	
		  	return new ModelMap("result",mp);
	
		
	}
	
	@RequestMapping(value="/listcols.do")
	public ModelMap collection(@RequestParam(value = "loc", required = false) String handle) throws Exception{
		Map<String,Object> mp = new HashMap<String,Object>();
		
		SearchParameters sp = new SearchParameters();
		sp.setUrl("http://localhost:8080/crosswalk");
		sp.setIdentifier(handle);
		PluginInterface pi = (PluginInterface)Class.forName("org.un.undesa.bungeni.crosswalk.search.DspacePlugin").newInstance();
		
	  	mp.put("results", /*community*//*comm*/pi.getDetails(sp));	  	
	  	return new ModelMap("result",mp);
	}
	
	/*@RequestMapping("/index.do")
	public ModelMap index(HttpServletRequest request, @RequestParam(value = "cn", required = false) String cn)  throws Exception{
		Map<String,Object> mp = new HashMap<String,Object>();
	  	mp.put("results", "testing");
	  	
	  	//JSONSerializer serializer = new JSONSerializer();
	  	//String jsonResult = new flexjson.JSONSerializer().serialize(cars);

	    //return serializer.serialize( person );
	    //return new JSONSerializer().include("hobbies").serialize( person );



	  	
	  	return new ModelMap("result",mp);
	}*/
	
	@RequestMapping("/index.do")
	public ModelMap  search(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "author", required = false) String query1,
			@RequestParam(value = "abstract", required = false) String query2, @RequestParam(value = "subject", required = false) String query3, 
			@RequestParam(value = "title", required = false) String query4, @RequestParam(value = "dop", required = false) String dop, 
			@RequestParam(value = "language", required = false) String query6, @RequestParam(value = "keywords", required = false) String query7,
			@RequestParam(value = "description", required = false) String query8, @RequestParam(value = "start", required = false) String start,
			@RequestParam(value = /*"pagesize"*//*"limit"*/"rows", required = false) String pagesize, 
			@RequestParam(value = /*"page"*/"start", required = false) String page,
			@RequestParam(value = /*"rows"*/"rows", required = false) String rows, @RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "sidx", required = false) String soridx, @RequestParam(value = "sord", required = false) String sorttyp,
			@RequestParam(value = "loc", required = false) String loc, @RequestParam(value = "code", required = false) String year  ) throws Exception{
		
		if(type==null) {System.out.println("outputting null");return null;}
		System.out.println("\n\ntypes are: "+type);	
		
		
		Resource config = this.resourceLoader.getResource(this.configLocation);
		Resource repository = this.resourceLoader.getResource(this.repositoriesLocation);
		SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");	  	
	  	//Map storing the plugin id and the plugin class resp.
		//cannot use Map<String,String> as it does not support iteration, which we'll be 
		//using in the advanced search
	  	IterableMap plugin = new HashedMap();
	  	try {
			org.jdom.Document jdomDocument = saxBuilder.build(config.getInputStream());
			org.jdom.Element el = jdomDocument.getRootElement();
			List<org.jdom.Element> allChildren = el.getChildren();
			for(int i=0;i<allChildren.size();i++) {
				org.jdom.Element element = (org.jdom.Element)allChildren.get(i);
				plugin.put(element.getAttributeValue("id"), element.getAttributeValue("class"));
			}
	  	}catch(Exception ex) {
	  		ex.printStackTrace();
	  	}	  	
	  	
	  	
		SAXBuilder saxBuilder2 = new SAXBuilder("org.apache.xerces.parsers.SAXParser");		
	  	//Map storing the repositories we are authoritative over <url, family>
		//Have to use this kind of map since there might be multiple repositories (keys) for the same family!
		MultiHashMap repositories = new MultiHashMap();
		try {
			org.jdom.Document jdomDocument = saxBuilder2.build(repository.getInputStream());
			org.jdom.Element el = jdomDocument.getRootElement();
			List<org.jdom.Element> allChildren = el.getChildren();
			for(int i=0;i<allChildren.size();i++) {
				org.jdom.Element element = (org.jdom.Element)allChildren.get(i);
				repositories.put(element.getAttributeValue("family"), element.getAttributeValue("url"));
			}
	  	}catch(Exception ex) {
	  		ex.printStackTrace();
	  	}
	  	
	  	System.out.println("\n\n\n\n\ndop: "+dop);
	  	//Generate the SearchParamenters
	  	SearchParameters sp = injectParameters(query1, query2, query3, query4, dop, query6, query7, start, pagesize, page, rows, soridx, sorttyp, loc, null, year);//TODO: add library url
	  	System.out.println("\n\n\n\n\ndop: "+sp.getIssue_date_string());
	  	//Where we'll be putting our results
	  	SearchResults sr = null;
	  		  	
	  	//If no plugins are available, we cannot do anything
	  	if(plugin.isEmpty())return null;
	  	
	  	//is it an advanced search?
	  	if(type.equalsIgnoreCase("all")) {
	  		//perform an advanced (union) search
	  		MapIterator it = plugin.mapIterator();
	  		while (it.hasNext()) {
	  		   String id = (String)it.next();
	  		   String classString = (String)it.getValue();
	  		   PluginInterface pi = (PluginInterface)Class.forName(classString).newInstance();
	  		   
	  		   Iterator<String> iterator = repositories.iterator(id);
	  		   while(iterator.hasNext()) {
	  			   String url = (String) iterator.next();
	  			   sp.setUrl(url);
	  			   SearchResults localResults = pi.search(sp);
	  			   List<SearchResult> st = new ArrayList<SearchResult>();
	  			   try {
	  				   //We modify the urls to the items so as to work across plugins and repositories (universal id)
	  				   for(SearchResult srt: localResults.getSearchResults()) {
	  					   //System.out.println("\n\nChanging urls");
	  					   String murl = url;
	  					   murl = murl.replace("crosswalk", "jspui");
	  					   
	  					   System.out.println("\n\nmurl: "+murl);
	  					   
	  					   
	  					   srt.setUrl(id+":"+srt.getUrl()+":"+murl);// {family:id:url}
	  					   
	  					   System.out.println(srt.getUrl());
	  					   
	  					   st.add(srt);
	  				   }
	  				   localResults.setSearchResults(localResults.getSearchResults()==null?null:st);
	  			   }catch(Exception ex) {
	  				   //System.out.println("\n\nChanging urls failed");
	  				   ex.printStackTrace();
	  			   }
	  			   sr = unify(sr, localResults);
	  			   //System.out.println("\n\nSearchResults returned");
	  		   }	  		   
	  		}
	  	}else {
	  		//Iterate through the available plugins to see if at least on can satisfy the search
	  		if(plugin.containsKey(type)) {
	  			System.out.println("Compatible plugin found");
	  			//We can't blindly trust the xml file as the jar files (containing the classes) may be 
	  			//missing and the records still there. Let's be cautious to avoid our program going bust :-)
	  			try {	  				 
	  				PluginInterface pi = (PluginInterface)Class.forName(((String)plugin.get(type))).newInstance();
	  				
	  				//Iterate the libraries and performing searches on compatible families
	  				Iterator<String> url = repositories.iterator(type);
//int ii = 0;
//	  				while(url.hasNext()) {
//System.out.println("\n\n"+ii);
//ii++;
//	  					String repoUrl = (String) url.next();
//	  					sp.setUrl(repoUrl);
//	  					SearchResults localResults = pi.search(sp);
//	  					List<SearchResult> st = new ArrayList<SearchResult>();
//	 	  			   try {
//System.out.println("\n\n"+ii);
//	 	  				   //We modify the urls to the items so as to work across plugins and repositories (universal id)
//	 	  				   for(SearchResult srt: localResults.getSearchResults()) {
//System.out.println("\n\n"+ii);
//	 	  					   System.out.println("\n\nChanging urls");
//	 	  					   srt.setUrl(type+":"+srt.getUrl()+":"+repoUrl);// {family:id:url}
//	 	  					   st.add(srt);
//	 	  				   }
//	 	  				   localResults.setSearchResults(st);
//	 	  			   }catch(Exception ex) {
//	 	  				   System.out.println("\n\nChanging urls failed");
//	 	  				   ex.printStackTrace();
//	 	  			   }
//		  				sr = unify(sr, localResults);
//		  				System.out.println("\n\nSearchResults returned");
//	  				}

int ii = 0;
//System.out.println("\n\n"+ii);
ii++;
	  					String repoUrl = (String) url.next();
	  					sp.setUrl(repoUrl);
	  					SearchResults localResults = pi.search(sp);
	  					List<SearchResult> st = new ArrayList<SearchResult>();
	 	  			   try {
//System.out.println("\n\n"+ii);
	 	  				   //We modify the urls to the items so as to work across plugins and repositories (universal id)
	 	  				   for(SearchResult srt: localResults.getSearchResults()) {
//System.out.println("\n\n"+ii);
	 	  					   String murl = repoUrl;
		  					   murl = murl.replace("crosswalk", "jspui");
		  					   
		  					 System.out.println("\n\nmurl: "+murl);
		  					 
		  					 
	 	  					   //System.out.println("\n\nChanging urls");
	 	  					   srt.setUrl(type+":"+srt.getUrl()+":"+murl);// {family:id:url}
	 	  					
	 	  					   System.out.println(srt.getUrl());
	 	  					   
	 	  					   //srt.setTitle(java.net.URLEncoder.encode(srt.getTitle(),"UTF-8"));
	 	  					   //srt.setAuthor(java.net.URLEncoder.encode(srt.getAuthor(),"UTF-8"));
	 	  					   st.add(srt);
	 	  				   }
	 	  				localResults.setSearchResults(localResults.getSearchResults()==null?null:st);
	 	  			   }catch(Exception ex) {
	 	  				   //System.out.println("\n\nChanging urls failed");
	 	  				   localResults.setMaxPages(0);
	 	  				   localResults.setMaxResults(0);
	 	  				   localResults.setPageSize(30);
	 	  				   localResults.setStartRecord(0);
	 	  			   }
		  				sr = unify(sr, localResults);
		  				System.out.println("\n\nSearchResults returned");

	  				
	  			}catch(Exception ex) {
	  				System.out.println("\n\nOops, an error occured!");
	  				ex.printStackTrace();
	  			}	  			
	  		}else {
	  			//print that we do not have a compatible plugin in that family and die
	  			System.out.println("\nSorry, there's no compatible plugin available.");return null;
	  		}
	  	}
	  	//only for testing TODO: to be deleted
	  	System.out.println("\n\nb4 iter");
	  	try {
	  		if(sr.getSearchResults()==null) {
		  		System.out.println("\n\n\nSearchResult List null");
		  		/*List<SearchResult> st = new ArrayList<SearchResult>();
		  		st.add(new SearchResult());
		  		sr.setSearchResults(st);*/
		  	}else {
			  	for(SearchResult sres : sr.getSearchResults()) {
			  		System.out.println("Sr values:"+sres.getDetails());
			  		System.out.println("Sr values2:"+sres.getDescription());
			  	}
		  	}
	  	}catch(Exception ex) {
	  		/*List<SearchResult> st = new ArrayList<SearchResult>();
	  		st.add(new SearchResult());
	  		sr.setSearchResults(st);*/
	  		ex.printStackTrace();
	  	}
	  	System.out.println("\n\nafter iter");
	  	
	  	List<SearchResult> srt = new ArrayList<SearchResult>();
	  	try {
			for (SearchResult srt2 : sr.getSearchResults()) {
				if (srt2.getBitstream() == null) {
					List tmp = new ArrayList<TransferMap>();
					srt2.setBitstream(tmp);
				} else {
					srt2.setBitstreams();
				}
				/*srt2.setBitstreams();
				List tmp = new ArrayList<TransferMap>();
				srt2.setBitstream(tmp);*/
				srt.add(srt2);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		sr.setSearchResults(sr.getSearchResults()==new SearchResults()?null:srt);
	  	
	  	sr.setSearchTerms(query7 == null ? "Empty!" : query7);
	  	////JSONSerializer serializer = new JSONSerializer();
	  	//String jsonResult = new flexjson.JSONSerializer().serialize(sr);
	  	String jsonResult =  new flexjson.JSONSerializer().transform(new DateTransformer("yyyy/MM/dd"),"searchResults.issue_date").exclude("*.class").include("searchResults")/*.exclude("searchResults.bitstream")*//*.exclude("class")*/.serialize(sr); // send the entire graph starting at person 
	  	
	  	System.out.println("json is:"+jsonResult/*test*/);
	  	response.setContentType("text/javascript;charset=utf-8");
	  	response.setCharacterEncoding("UTF-8");
	  	     response.getWriter().write(jsonResult/*test*/);
	  	     return null;

	  	
	  	
	  	//If we're still alive, it means we have results (even zero).
	  	//Generate the response for the client
	  	/*Map<String,Object> mp = new HashMap<String,Object>();
	  	mp.put("results", sr);	  	
	  	return new ModelMap("result",mp); */
	  	
	}
	
	@RequestMapping(value="/icollection.do")
	public ModelMap icollection(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "url", required = false) String url, @RequestParam(value = "alternate", required = false) String alternate) throws Exception{
		
		HttpClient client = new HttpClient();
	    GetMethod method = new GetMethod(url+"/showCollections");
	    
	    String responsible = "";
	    try {
		    // Provide custom retry handler is necessary
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
			  		new DefaultHttpMethodRetryHandler(3, false));
			if(alternate!=null) {
				method.setQueryString(URIUtil.encodeQuery("alternate=true"));
			}			
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
	        System.out.println(responsible);
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

  	      System.out.println("\n\nb4 xstream");
	  	      XStream xstream = new XStream();
	  	    List<CommunityCollection> ccl = (List<CommunityCollection>) xstream.fromXML(responsible);
	  	    
	  	    List <TreeNodes> rooter2 = new ArrayList<TreeNodes>();
	  	    
	  	    TreeNodes tnb = new TreeNodes();
	  	    for(CommunityCollection ccfl : ccl) {
	  	    	tnb = new TreeNodes();
	  	    	tnb.setLeaf(false);
	  	    	tnb.setIconCls("task-folder");
	  	    	tnb.setCls("master-task");
	  	    	tnb.setText(ccfl.getCommunityName());
	  	    	tnb.setUri(ccfl.getCommunityHandle()/*.getCommunityName()*/);
	  	    	
	  	    	List <TreeNodes> rooter = null;
	  	    	for(CommunityCollection cc: ccfl.getCommunity()) {	  
	  	    		rooter = new ArrayList<TreeNodes>();
			  	    TreeNodes tn = new TreeNodes();
			  	    
		  	    	tn = new TreeNodes();
		  	    	System.out.println("\n\n1st for");
		  	    	tn.setLeaf(false);
		  	    	tn.setIconCls("task-folder");
		  	    	tn.setCls("master-task");
		  	    	tn.setText(cc.getCommunityName());
		  	    	tn.setUri(cc.getCommunityHandle());
		  	    	List<TreeNodes> tnl = new ArrayList<TreeNodes>();
		  	    	TreeNodes tn2 = new TreeNodes();
		  	    	if(cc.getCommunity() !=null) {
		  	    		System.out.println("\n\nif comm");
		  	    		for(CommunityCollection com: cc.getCommunity()) {
		  	    			tn2 = new TreeNodes();
		  	    			System.out.println("\n\nif comm for");
			  	    		tn2.setLeaf(false);
			  	    		tn2.setIconCls("task-folder");
				  	    	tn2.setText(com.getCommunityName());
				  	    	tn2.setUri(com.getCommunityHandle());
				  	    	List<TreeNodes> tnl2 = new ArrayList<TreeNodes>();
				  	    	TreeNodes tn3 = new TreeNodes();
				  	    	for(CollectionCollection com2: com.getCollection()) {
				  	    		tn3 = new TreeNodes();
				  	    		System.out.println("\n\nif comm for for");
				  	    		tn3.setLeaf(true);
				  	    		tn3.setIconCls("task");
				  	    		tn3.setText(com2.getCollectionName());System.out.println("\n\n\n"+com2.getCollectionName());
					  	    	tn3.setUri(com2.getCollectionHandle());System.out.println("\n\n\n"+com2.getCollectionHandle());
					  	    	tnl2.add(tn3);
				  	    	}
				  	    	/*if (!tnl2.isEmpty())*/tn2.setChildren(tnl2); 
				  	    	tnl.add(tn2);
			  	    	}
		  	    		/*if (!tnl.isEmpty())*/tn.setChildren(tnl);
			  	    	
		  	    	}else {
		  	    		System.out.println("\n\nelse");
		  	    		for(CollectionCollection com: cc.getCollection()) {
		  	    			tn2 = new TreeNodes();
		  	    			System.out.println("\n\nelse collection");
			  	    		tn2.setLeaf(true);
			  	    		tn2.setIconCls("task");
				  	    	tn2.setText(com.getCollectionName());
				  	    	tn2.setUri(com.getCollectionHandle());
				  	    	tnl.add(tn2);
			  	    	}
		  	    		/*if (!tnl.isEmpty())*/tn.setChildren(tnl);
		  	    	}
		  	    	rooter.add(tn);	
		  	    	tnb.setChildren(rooter);
	  	    	}	  	    	
	  	    	rooter2.add(tnb);
	  	    	
	  	    	
	  	    }
	  	    
	  	    
	  	  JSONSerializer serializer = new JSONSerializer();
		  	//String jsonResult = new flexjson.JSONSerializer().serialize(sr);
		  	String jsonResult = new flexjson.JSONSerializer().include("children").include("children.children").include("children.children.children").exclude("children.class").exclude("children.children.class").exclude("class").exclude("leaf").serialize(/*tn*/rooter2); // send the entire graph starting at person
		  	
		  	System.out.println("json is:"+jsonResult/*test*/);
		  	response.setContentType("application/json;charset=utf-8");
		  	response.setCharacterEncoding("UTF-8");
		  	     response.getWriter().write(jsonResult/*test*/);
		  	     return null;
	
		
	}

	@RequestMapping("/redirect.do")
	public ModelMap /*ModelAndView*/ redirect(HttpServletRequest request, @RequestParam(value = "url", required = false) String url) {
		Map<String,Object> mp = new HashMap<String,Object>();
		StringTokenizer st = new StringTokenizer(url, ":");
		int count = 0;
		String newUrl = "";
		String handle = "";
		while(st.hasMoreTokens()) {
			String str = st.nextToken();
			if(count==2) {
				newUrl = str;
			}else if(count == 1) {
				handle=str;
			}else if(count == 0) {
				if(!str.equalsIgnoreCase("dspace")) {return null;}
			}else if(count == 3) {
				newUrl = newUrl + ":" +str;
			}else if(count == 4) {
				newUrl = newUrl + ":" +str;
			}
			count++;
		}
		String result = newUrl+"/handle/"+handle;
		System.out.println("\n\n:"+result);
	  	mp.put("url", result);	  	
	  	return new ModelMap("result",mp);
	}
	
	@RequestMapping(value="/js.do")
	public ModelAndView js(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "js", required = false) String js, @RequestParam(value = "search", required = false) String search) throws Exception{
		//defaultHome
		ModelAndView mav = new ModelAndView("kit/"+js);
		buildHome();
		mav.addObject("defaultHome", defaultHome);
		mav.addObject("search", search);
		//return "bungeni/assets/js/"+js;
		return mav;
	}
	
	private void buildHome() {
		Resource repository = this.resourceLoader.getResource(this.repositoriesLocation);
		SAXBuilder saxBuilder2 = new SAXBuilder("org.apache.xerces.parsers.SAXParser");		
	  	//Map storing the repositories we are authoritative over <url, family>
		//Have to use this kind of map since there might be multiple repositories (keys) for the same family!
		MultiHashMap repositories = new MultiHashMap();
		try {
			org.jdom.Document jdomDocument = saxBuilder2.build(repository.getInputStream());
			org.jdom.Element el = jdomDocument.getRootElement();
			List<org.jdom.Element> allChildren = el.getChildren();
			for(int i=0;i<allChildren.size();i++) {
				org.jdom.Element element = (org.jdom.Element)allChildren.get(i);
				repositories.put(element.getAttributeValue("family"), element.getAttributeValue("url"));
				if(element.getAttributeValue("default") != null) {
					try {
						if(element.getAttributeValue("default").equalsIgnoreCase("true")) {
							defaultHome = element.getAttributeValue("url");
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
	  	}catch(Exception ex) {
	  		ex.printStackTrace();
	  	}
	}
	
	@RequestMapping(value="/reports.do")
	public void reports(HttpServletResponse response){
		Resource repository = this.resourceLoader.getResource(this.reportsLocation);
		SAXBuilder saxBuilder2 = new SAXBuilder("org.apache.xerces.parsers.SAXParser");		
	  	//Map storing the repositories we are authoritative over <url, family>
		//Have to use this kind of map since there might be multiple repositories (keys) for the same family!
		MultiHashMap repositories = new MultiHashMap();
		List<report> report = new ArrayList<report>();
		
		try {
			org.jdom.Document jdomDocument = saxBuilder2.build(repository.getInputStream());
			org.jdom.Element el = jdomDocument.getRootElement();
			List<org.jdom.Element> allChildren = el.getChildren();
			for(int i=0;i<allChildren.size();i++) {
				org.jdom.Element element = (org.jdom.Element)allChildren.get(i);
				report repo = new report();
				repo.setDescription(element.getAttributeValue("description"));
				repo.setUri(element.getAttributeValue("uri"));
				repo.setName(element.getAttributeValue("name"));
				
				report.add(repo);
				//repositories.put(element.getAttributeValue("name"), element.getAttributeValue("description"));
			}
	  	}catch(Exception ex) {
	  		ex.printStackTrace();
	  	}
	  	
	  	//JSONSerializer serializer = new JSONSerializer();
	  	String jsonResult = new flexjson.JSONSerializer().exclude("class").serialize(report);
	  	
	  	System.out.println("json is:"+jsonResult);
	  	response.setContentType("application/json;charset=utf-8");
	  	response.setCharacterEncoding("UTF-8");
	  	try {
			response.getWriter().write(jsonResult);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	
	}
	
	public class report {
		public String description;
		public String uri;
		public String name;
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}		
	}
	
	public class language {
		public String name;
		public String description;
		public String code;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}		
	}
	
	@RequestMapping(value="/languages.do")
	public void languages(HttpServletResponse response){
		Resource repository = this.resourceLoader.getResource(this.languagesLocation);
		SAXBuilder saxBuilder2 = new SAXBuilder("org.apache.xerces.parsers.SAXParser");		
	  	//Map storing the repositories we are authoritative over <url, family>
		//Have to use this kind of map since there might be multiple repositories (keys) for the same family!
		MultiHashMap repositories = new MultiHashMap();
		List<language> report = new ArrayList<language>();
		
		try {
			org.jdom.Document jdomDocument = saxBuilder2.build(repository.getInputStream());
			org.jdom.Element el = jdomDocument.getRootElement();
			List<org.jdom.Element> allChildren = el.getChildren();
			for(int i=0;i<allChildren.size();i++) {
				org.jdom.Element element = (org.jdom.Element)allChildren.get(i);
				language lang = new language();
				lang.setCode(element.getAttributeValue("code"));
				lang.setName(element.getAttributeValue("name"));
				lang.setDescription(element.getAttributeValue("description"));
				
				report.add(lang);
				//repositories.put(element.getAttributeValue("name"), element.getAttributeValue("description"));
			}
	  	}catch(Exception ex) {
	  		ex.printStackTrace();
	  	}
	  	
	  	//JSONSerializer serializer = new JSONSerializer();
	  	String jsonResult = new flexjson.JSONSerializer().exclude("class").serialize(report);
	  	
	  	System.out.println("json is:"+jsonResult);
	  	response.setContentType("application/json;charset=utf-8");
	  	response.setCharacterEncoding("UTF-8");
	  	try {
			response.getWriter().write(jsonResult);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/years.do")
	public void years(HttpServletResponse response){
		String result = "[";
	  	for(int i=1900; i<=(new Date()).getYear()+1900; i++){
	  		if(i==1900){
	  			result +="{year: \""+i+"\", name: \"English\"}";
	  		}else{
	  			result +=", {year: \""+i+"\", name: \"English\"}";
	  		}
			System.out.println("\ndate:"+i);
		}
	  	result +="]";
	  	System.out.println(result);
	  	
	  	System.out.println("json is:"+result);
	  	response.setContentType("application/json;charset=utf-8");
	  	response.setCharacterEncoding("UTF-8");
	  	try {
			response.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
