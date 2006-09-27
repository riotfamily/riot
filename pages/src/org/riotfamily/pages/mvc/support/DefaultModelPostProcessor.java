package org.riotfamily.pages.mvc.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.mvc.ModelPostProcessor;

/**
 *
 */
public class DefaultModelPostProcessor implements ModelPostProcessor {
	
	private Object value;
	
	private String modelKey;
	
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void postProcess(Map model, HttpServletRequest request)
			throws Exception {

		if (model.get(modelKey) == null) {
			model.put(modelKey, value);
		}
	}
}
