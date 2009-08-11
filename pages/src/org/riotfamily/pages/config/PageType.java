package org.riotfamily.pages.config;

import java.util.List;

import org.riotfamily.common.util.FormatUtils;

public class PageType {

	private String name;
	
	private String label;
	
	private List<PageType> childTypes;
	
	private Object handler;
	
	private List<String> suffixes;

	public PageType() {
	}

	public PageType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLabel() {
		if (label == null) {
			label = FormatUtils.xmlToTitleCase(name);
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Object getHandler() {
		return handler;
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}
	
	public List<String> getSuffixes() {
		return suffixes;
	}

	public void setSuffixes(List<String> suffixes) {
		this.suffixes = suffixes;
	}
	
	public void setSuffix(String suffix) {
		this.suffixes = FormatUtils.tokenizeCommaDelimitedList(suffix);
	}

	public List<PageType> getChildTypes() {
		return childTypes;
	}

	public void setChildTypes(List<PageType> childTypes) {
		this.childTypes = childTypes;
	}
	
	void register(SitemapSchema schema) {
		schema.addType(this);
		if (childTypes != null) {
			for (PageType type : childTypes) {
				type.register(schema);
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof PageType) {
			PageType other = (PageType) obj;
			return name.equals(other.name);
		}
		return false;
	}
}
