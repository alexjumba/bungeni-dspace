package org.un.undesa.bungeni.crosswalk.search;

import java.util.Date;
import java.util.*;

public class SearchResult implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String title;
	private String[] authors;
	private String[] keywords;
	private String[] details;
	private Date issue_date;
	private String publisher;
	private String citation;
	private String Abstract;
	private String description;
	private String uri = "book.jpg";
	private String score;
	private String hitNumber; 
	private String defaults;
	private String sponsor;
	private String url;
	private String fakeAuthor;
	private String collection; //should be string[]
	private String bitstreams;
	private String language;
	private List<TransferMap> bitstream;
	
	public SearchResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String[] getAuthors() {
		return authors;
	}
	public void setAuthors(String[] authors) {
		this.authors = authors;
	}
	public String[] getKeywords() {
		return keywords;
	}
	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}
	public Date getIssue_date() {
		return issue_date;
	}
	public void setIssue_date(Date issue_date) {
		this.issue_date = issue_date;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getCitation() {
		return citation;
	}
	public void setCitation(String citation) {
		this.citation = citation;
	}
	public String getAbstract() {
		return Abstract;
	}
	public void setAbstract(String abstract1) {
		Abstract = abstract1;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		//this.description = this.description == null? "" : this.description + " " +description;
		this.description = description;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getHitNumber() {
		return hitNumber;
	}

	public void setHitNumber(String hitNumber) {
		this.hitNumber = hitNumber;
	}

	public String getDefaults() {
		return defaults;
	}

	public void setDefaults(String defaults) {
		this.defaults = defaults;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setAuthor(String author) {
		this.fakeAuthor = author;
	}
	
	public String getAuthor() {
		if(this.authors == null) {
			return this.fakeAuthor;
		}
		String string = "";
		for(String str: this.authors) {
			string = string + str + " ";
		}
		return string;
	}

	public String getDetails() {		
		/*if(this.description !=null) {
			return this.description;
		}
		if(this.details == null) {
			return null;
		}
		String string = "";
		for(String str: this.details) {
			string = string + str + " ";
		}*/
		//return string;
		return this.description;
	}

	public void setDetails(String[] details) {
		this.details = details;
	}

	public String getBitstreams() {
		return bitstreams;
		/*String st = "<ul class=\"bitstreams\">";
		for(TransferMap tm: getBitstream()) {
			//st+="<li><a href=\""+tm.getValue()+"\">"+tm.getKey()+"</a>";
			st +="<li>"+tm.getKey()+"</li>";
		}
		st+="</ul>";
		//return bitstreams;
		return st;*/
	}

	public void setBitstreams() {
		String st = "<ul class=\"bitstreams\">";
		for(TransferMap tm: getBitstream()) {
			//st+="<li><a href=\""+tm.getValue()+"\">"+tm.getKey()+"</a>";
			st +="<li>"+tm.getKey()+"</li>";
		}
		st+="</ul>";
		this.bitstreams = st;
	}
	
	public void setBitstreams(String bitstreams) {
		this.bitstreams = bitstreams;
	}

	public List<TransferMap> getBitstream() {
		return bitstream;
	}

	public void setBitstream(List<TransferMap> bitstream) {
		if(this.bitstream == null) {
			this.bitstream = bitstream;
		}else {
			List<TransferMap> bs = new ArrayList<TransferMap>();
			for(TransferMap tm: this.bitstream) {
				bs.add(tm);
			}
			for(TransferMap tm: bitstream) {
				bs.add(tm);
			}
			this.bitstream = bs;
		}
		
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	
	
	
}
