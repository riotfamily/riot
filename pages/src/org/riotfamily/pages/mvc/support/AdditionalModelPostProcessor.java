package org.riotfamily.pages.mvc.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.mvc.ModelBuilder;
import org.riotfamily.pages.mvc.ModelPostProcessor;

/**
 * ModelPostProcessor that adds the result of an additional ModelBuilder to
 * the given model. Note that the second ModelBuilder has no influence on
 * the cache-key or the last-modified date.
 */
public class AdditionalModelPostProcessor implements ModelPostProcessor {
	
	private ModelBuilder modelBuilder;
	
	public void setModelBuilder(ModelBuilder modelBuilder) {
		this.modelBuilder = modelBuilder;
	}
	
	public void postProcess(Map model, HttpServletRequest request)
			throws Exception {

		Map additionalModel = modelBuilder.buildModel(request);
		model.putAll(additionalModel);
	}

}
