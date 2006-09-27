package org.riotfamily.riot.hibernate.support;

public class SearchFilterBean {

	private String search;

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
        this.search = search;
	}
	
	public String getWildcardSearch() {
		if (search != null) {
			return "%" + search.toLowerCase() + '%';
		}
		return null;
	}

}
