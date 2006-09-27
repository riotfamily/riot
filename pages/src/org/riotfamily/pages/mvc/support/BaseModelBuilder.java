package org.riotfamily.pages.mvc.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.pages.mvc.ModelBuilder;

/**
 * Abstract base class for ModelBuilders that expose only a single object. 
 * The built model will be a {@link org.riotfamily.common.collection.FlatMap}.
 */
public abstract class BaseModelBuilder implements ModelBuilder {

	private String modelKey;
	
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	public final Map buildModel(HttpServletRequest request) throws Exception {
		FlatMap model = new FlatMap();
		model.put(modelKey, buildModelObject(request));
		return model;
	}
	
	public abstract Object buildModelObject(HttpServletRequest request) 
			throws Exception; 

}
