package org.un.undesa.bungeni.crosswalk.search;

// require
import java.io.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.FilterIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.ParseException;
import org.springframework.stereotype.Component;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.FuzzyQuery;
/*import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;*/

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;

import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;

import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import org.dspace.search.DSAnalyzer;

@Component("searcher")
public class Search implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2007478367751774879L;

	public Search() {
		
	}
	
	public SearchResults searchDSpace(SearchParameters sp, int startRecord, int maxResults) throws IOException, ParseException {
		
		String searchQuery = sp.getAuthors();//generateQuery(sp);;
		if(searchQuery == null || searchQuery.trim().equalsIgnoreCase("")) {
			System.out.println("\nSearch string must be specified");
			return null;
		}
		
		// initialize
		String field       = "default";						//for DSPace
		String queries     = searchQuery;//"Michael";//args[ 0 ];
		IndexReader reader = IndexReader.open( "search" ); /*index*/		// TODO:replace with properties file
		Searcher searcher  = new IndexSearcher( reader );
		Analyzer oldanalyzer  = new StandardAnalyzer();
		Analyzer analyzer = new DSAnalyzer();
		QueryParser parser = new QueryParser( field, analyzer );
		parser.setDefaultOperator(QueryParser.OR_OPERATOR);
		
		
		String fields[] = {"default","title","language","author","sponsor","sort_dateissued","handletext"};
		//QueryParser qry = new MultiFieldQueryParser(fields, analyzer);
        QueryParser qry = new QueryParser("default", analyzer);
		
		// create a query, search, and display number of hits
		//FuzzyQuery query = /*parser*/new FuzzyQuery(new Term(field, /*searchQuery*/"tannenbaum"), 0.8f, 2);//qry.parse( queries );
		Query query = /*parser*/qry.parse( queries );
		//Hits hits = searcher.search( query );
		//Query query = new TermQuery(new Term(/*"default"*/field, /*"Zane"*/searchQuery));
		Hits hits = searcher.search( query );
		System.out.println( "Your search (" + query.toString( field ) + ") found " + hits.length() + " documents." );    
	
		int start = Integer.parseInt(Integer.toString(startRecord));
        int end = Math.min(hits.length(), start + maxResults - 1);
        
        SearchResults searchResults = new SearchResults();
        searchResults.setMaxResults(hits.length());
        searchResults.setStartRecord(startRecord);
        
        List<SearchResult> results = new ArrayList<SearchResult>();
		for(int i = startRecord ; i<= end; i++ ) {
			System.out.println("\n\nFirst loop");
			SearchResult sr = new SearchResult();
			Document doc = hits.doc(i-1);
			// TODO: Add catchers for nulls
			sr.setHitNumber(String.valueOf(i));
			sr.setScore(String.valueOf(hits.score(i-1)));
			
			for (int j = 0; j != hits.length() && j != maxResults; ++j) {
                final Document docu = hits.doc(j);
                /*System.out.println("\nloop -1 > "+doc.getField("default").stringValue());
                System.out.println("\nloop -1 > "+doc.getField("author").stringValue());
                System.out.println("\nloop -1 > "+doc.getField("type").stringValue());
                
                System.out.println("\n\nloop -1 > "+doc.get("default"));
                System.out.println("\n\nloop -1 > "+doc.get("author"));
                System.out.println("\n\nloop -1 > "+doc.get("type"));*/
                List lst = (List) docu.getFields();
			     Iterator it = lst.iterator();
			     while(it.hasNext()) {
			    	 Field fls = (Field) it.next();
			    	 System.out.println("2\n\nFields -> "+ fls.name() + "   " + fls.stringValue());
			    	 
			     }
                
            }
			
			

			
			for (Enumeration e = doc.fields(); e.hasMoreElements(); ) {
				System.out.println("\n\nSecond loop");
                Field f = (Field)e.nextElement();
                // TODO: again unoptimized
                System.out.println("\n\nFound Field name "+f.name()+" Field Value "+f.stringValue());
                if(f.name().equalsIgnoreCase("author")) {
                	String[] array = null;
                	array[0] = f.stringValue();
                	sr.setAuthors(array);
                }else if(f.name().equalsIgnoreCase("keyword")) {
                	String[] array = null;
                	array[0] = f.stringValue();
                	sr.setKeywords(array);
                }else if(f.name().equalsIgnoreCase("default")) {
                	sr.setDefaults(f.stringValue());
                	System.out.println("\n\n\nDefaults set to "+sr.getDefaults());
                }else if(f.name().equalsIgnoreCase("title")) {
                	sr.setTitle(f.stringValue());
                }else if(f.name().equalsIgnoreCase("publisher")) {
                	sr.setPublisher(f.stringValue());
                }else if(f.name().equalsIgnoreCase("citation")) {
                	sr.setCitation(f.stringValue());
                }else if(f.name().equalsIgnoreCase("abstract")) {
                	sr.setAbstract(f.stringValue());
                }else if(f.name().equalsIgnoreCase("description")) {
                	sr.setDescription(f.stringValue());
                }else if(f.name().equalsIgnoreCase("search.resourceid")) {
                	sr.setUri(f.stringValue());
                	System.out.println("\n\n\nUri Handle is >"+ sr.getUri());
                }else if(f.name().equalsIgnoreCase("handle")) {
                	sr.setUrl(f.stringValue());
                	System.out.println("\n\n\nUrl Handle is >"+ sr.getUrl());
                }
            }
			results.add(sr);
		}
		
		TopDocCollector collector = new TopDocCollector(maxResults);
		   searcher.search(query, collector);
		   ScoreDoc[] hitz = collector.topDocs().scoreDocs;
		   for (int k = 0; k < hitz.length; k++) {
		     int docId = hitz[k].doc;
		     Document d = searcher.doc(docId);
		     
		     List lst = (List) d.getFields();
		     Iterator it = lst.iterator();
		     while(it.hasNext()) {
		    	 Field fls = (Field) it.next();
		    	 System.out.println("\n\nFields -> "+ fls.name() + "   " + fls.stringValue());
		     }
		     
		     System.out.println("\n\n\nNew -> "+d.getFields());
		     // do something with current hit
		   }
		searchResults.setSearchResults(results);
		
		// display each hit
		/*for ( int i = 0; i < hits.length(); i++ ) {		
			Document doc = hits.doc( i );
			System.out.println( "       Result: " + i );
			System.out.println( "       Author: " + doc.get( "author" ));
			System.out.println( "        Title: " + doc.get( "title" ));
			System.out.println( "  Call number: " + doc.get( "callnumber" ));
			System.out.println( "  Default: " + doc.get( "default" ));
			System.out.println( "" );			
		}*/
		
		return searchResults;
	}
	
	public SearchResults searchKoha(String searchString) {
		return null;
	}
	
	public String generateQuery(SearchParameters sp) {
		
		StringBuffer sb = new StringBuffer();
		String keyword,authors,title,subject,Abstract,series,sponsor,identifier,language;
		// TODO: quite long for now. Will replace with an array loop. Needs optimizing
		if((keyword = sp.getKeyword()) != null) {
			StringTokenizer st  = new StringTokenizer(keyword, " ");
			int count = 0,count2=0;
			while(st.hasMoreTokens()) {
				if(count==0) {
					sb.append("keyword:"+"\"");
					count++;
				}
				if(count2 == 0) {
					sb.append(st.nextToken());
					count2++;
				}
				else {
					sb.append(" AND "+st.nextToken());
				}
			}
			sb.append("\" ");
		}
		if((authors = sp.getAuthors()) != null) {
			StringTokenizer st  = new StringTokenizer(authors, " ");
			int count = 0,count2=0;
			while(st.hasMoreTokens()) {
				if(count==0) {					
					// TODO: change back to author
					//sb.append("author:"+"\"");
					sb.append("default:"+"\"");
					count++;
				}
				if(count2 == 0) {
					sb.append(st.nextToken());
					count2++;
				}
				else {
					sb.append(" AND "+st.nextToken());
				}
			}
			sb.append("\" ");
		}
		if((title = sp.getTitle()) != null) {
			StringTokenizer st  = new StringTokenizer(title, " ");
			int count = 0,count2=0;
			while(st.hasMoreTokens()) {
				if(count==0) {
					sb.append("title:"+"\"");
					count++;
				}
				if(count2 == 0) {
					sb.append(st.nextToken());
					count2++;
				}
				else {
					sb.append(" AND "+st.nextToken());
				}
			}
			sb.append("\" ");
		}
		if((subject = sp.getSubject()) != null) {
			StringTokenizer st  = new StringTokenizer(subject, " ");
			int count = 0,count2=0;
			while(st.hasMoreTokens()) {
				if(count==0) {
					sb.append("subject:"+"\"");
					count++;
				}
				if(count2 == 0) {
					sb.append(st.nextToken());
					count2++;
				}
				else {
					sb.append(" AND "+st.nextToken());
				}
			}
			sb.append("\" ");
		}
		if((Abstract = sp.getAbstract()) != null) {
			StringTokenizer st  = new StringTokenizer(Abstract, " ");
			int count = 0,count2=0;
			while(st.hasMoreTokens()) {
				if(count==0) {
					sb.append("abstract:"+"\"");
					count++;
				}
				if(count2 == 0) {
					sb.append(st.nextToken());
					count2++;
				}
				else {
					sb.append(" AND "+st.nextToken());
				}
			}
			sb.append("\" ");
		}
		if((series = sp.getSeries()) != null) {
			StringTokenizer st  = new StringTokenizer(series, " ");
			int count = 0,count2=0;
			while(st.hasMoreTokens()) {
				if(count==0) {
					sb.append("series:"+"\"");
					count++;
				}
				if(count2 == 0) {
					sb.append(st.nextToken());
					count2++;
				}
				else {
					sb.append(" AND "+st.nextToken());
				}
			}
			sb.append("\" ");
		}
		if((sponsor = sp.getSponsor()) != null) {
			StringTokenizer st  = new StringTokenizer(sponsor, " ");
			int count = 0,count2=0;
			while(st.hasMoreTokens()) {
				if(count==0) {
					sb.append("sponsor:"+"\"");
					count++;
				}
				if(count2 == 0) {
					sb.append(st.nextToken());
					count2++;
				}
				else {
					sb.append(" AND "+st.nextToken());
				}
			}
			sb.append("\" ");
		}
		if((identifier = sp.getIdentifier()) != null) {
			StringTokenizer st  = new StringTokenizer(identifier, " ");
			int count = 0,count2=0;
			while(st.hasMoreTokens()) {
				if(count==0) {
					sb.append("identifier:"+"\"");
					count++;
				}
				if(count2 == 0) {
					sb.append(st.nextToken());
					count2++;
				}
				else {
					sb.append(" AND "+st.nextToken());
				}
			}
			sb.append("\" ");
		}
		if((language = sp.getLanguage()) != null) {
			StringTokenizer st  = new StringTokenizer(language, " ");
			int count = 0,count2=0;
			while(st.hasMoreTokens()) {
				if(count==0) {
					sb.append("language:"+"\"");
					count++;
				}
				if(count2 == 0) {
					sb.append(st.nextToken());
					count2++;
				}
				else {
					sb.append(" AND "+st.nextToken());
				}
			}
			sb.append("\" ");
		}		
		
		String results = sb.toString();
		System.out.println("\nGenerated Query String: "+results);
		return results;
	} 

}
