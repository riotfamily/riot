package org.riotfamily.website.generic.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class StaticModelBuilder implements ModelBuilder {

	private Map<String,Object> model;
	
	public void setModel(Map<String,Object> model) {
		this.model = model;
	}

	public Map<String,Object> buildModel(HttpServletRequest request) {
		return model;
	}
}
