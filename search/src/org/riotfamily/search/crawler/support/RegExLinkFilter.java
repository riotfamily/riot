package org.riotfamily.search.crawler.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.riotfamily.search.crawler.LinkFilter;


public class RegExLinkFilter implements LinkFilter {	
	
	private List includes;
	
	private List excludes;
	
	public void setIncludes(String[] patterns) {
		includes = new ArrayList(patterns.length);
		for (int i = 0; i < patterns.length; i++) {
			includes.add(Pattern.compile(patterns[i]));
		}	
	}
	
	public void setExcludes(String[] patterns) {
		excludes = new ArrayList(patterns.length);
		for (int i = 0; i < patterns.length; i++) {
			excludes.add(Pattern.compile(patterns[i]));
		}	
	}
	
	public boolean accept(String baseUrl, String link) {
		if (anyMatch(includes, link)) {
			return !anyMatch(excludes, link);
		}
		return false;
	}
	
	private boolean anyMatch(Collection patterns, String link) {
		if (patterns != null) {
			Iterator it = patterns.iterator();
			while (it.hasNext()) {
				Pattern pattern = (Pattern) it.next();
				if (pattern.matcher(link).matches()) {
					return true;			
				}
			}
		}
		return false;
	}
	
}
