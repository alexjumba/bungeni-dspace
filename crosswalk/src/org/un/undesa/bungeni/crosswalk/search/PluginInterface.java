package org.un.undesa.bungeni.crosswalk.search;

public interface PluginInterface {
	public SearchResults search(SearchParameters query) throws Exception;
	public SearchResults getDetails(SearchParameters sp) throws Exception;
}
