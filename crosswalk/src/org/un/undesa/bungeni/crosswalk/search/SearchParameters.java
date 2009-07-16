package org.un.undesa.bungeni.crosswalk.search;

import java.util.*;
public class SearchParameters implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String keyword;
	private String authors;
	private String title;
	private String subject;
	private String Abstract;
	private String series;
	private String sponsor;
	private String identifier;
	private String language;
	private String url;
	private Date issue_date;
	private String issue_date_string;
	private String description;
	
	private String sortKey;
	private String sortType;
	private String startOffset;
	private String retrieveNumber;
	private String location;
	private String alternate;
	private String parliament;
	private String year;
	
	public SearchParameters() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getAuthors() {
		return authors;
	}
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAbstract() {
		return Abstract;
	}
	public void setAbstract(String abstract1) {
		Abstract = abstract1;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getSponsor() {
		return sponsor;
	}
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public String getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(String startOffset) {
		this.startOffset = startOffset;
	}

	public String getRetrieveNumber() {
		return retrieveNumber;
	}

	public void setRetrieveNumber(String retrieveNumber) {
		this.retrieveNumber = retrieveNumber;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getIssue_date() {
		return issue_date;
	}

	public void setIssue_date(Date issue_date) {
		this.issue_date = issue_date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIssue_date_string() {
		return issue_date_string;
	}

	public void setIssue_date_string(String issueDateString) {
		issue_date_string = issueDateString;
	}

	public String getAlternate() {
		return alternate;
	}

	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}

	public String getParliament() {
		return parliament;
	}

	public void setParliament(String parliament) {
		this.parliament = parliament;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
}
