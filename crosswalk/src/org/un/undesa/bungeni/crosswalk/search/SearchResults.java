package org.un.undesa.bungeni.crosswalk.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResults implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int startRecord; 
    private int maxResults;
    private int maxPages;
    private int pageSize;
    private String searchTerms;
    private String searchDescription = "Search Results";
    private List<SearchResult> searchResults;    
    
	public SearchResults() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getStartRecord() {
		return startRecord;
	}
	public void setStartRecord(int startRecord) {
		this.startRecord = startRecord;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<SearchResult> getSearchResults() {
		return searchResults;
	}
	public void setSearchResults(List<SearchResult> searchResults) {
		this.searchResults = searchResults;
	}
	public int getMaxPages() {
		return maxPages;
	}
	public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}
	public String getSearchDescription() {
		return searchDescription;
	}
	public void setSearchDescription(String searchDescription) {
		this.searchDescription = searchDescription;
	}
	public String getSearchTerms() {
		return searchTerms;
	}
	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}
	
	
    
    
}
