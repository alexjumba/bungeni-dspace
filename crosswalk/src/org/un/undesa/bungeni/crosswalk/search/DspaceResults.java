package org.un.undesa.bungeni.crosswalk.search;


import org.dspace.search.*;
import org.dspace.content.*;

import java.util.*;

public class DspaceResults {
	private QueryResults results;
	private List<Item> item;
	
	public DspaceResults() {
		
	}
	
	public QueryResults getResults() {
		return results;
	}

	public void setResults(QueryResults results) {
		this.results = results;
	}

	public List<Item> getItem() {
		return item;
	}

	public void setItem(List<Item> item) {
		this.item = item;
	}

	
}
