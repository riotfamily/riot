package org.riotfamily.website.generic.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class StaticModelBuilder implements ModelBuilder {

	private Map model;
	
	public void setModel(Map model) {
		this.model = model;
	}

	public Map buildModel(HttpServletRequest request) {
		return model;
	}
}
