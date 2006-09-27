package org.riotfamily.pages.mvc;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.mvc.cache.AbstractCachingPolicyController;
import org.riotfamily.pages.mvc.cache.CacheableModelBuilder;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Generic controller implementation that utilizes a ModelBuilder to create
 * a model which is passed on to a view with a configurable name. Additionally
 * ModelPostProcessors can be registered to tweak the model.
 */
public class GenericController extends AbstractCachingPolicyController {

	/** The ModelBuilder */
	private ModelBuilder modelBuilder;
	
	/** Set if modelBuilder is cacheable to reduce number of casts */
	private CacheableModelBuilder cacheableModelBuilder;
		
	/** ModelPostProcessors */
	private ModelPostProcessor[] postProcessors;
		
	/** View name to use */
	private String viewName;
	
	private String contentType;
			
	private boolean addUriToCacheKey;
	
	public GenericController() {
	}
	
	public GenericController(ModelBuilder modelBuilder) {
		this.modelBuilder = modelBuilder;
	}
	
	public void setModelBuilder(ModelBuilder modelBuilder) {
		this.modelBuilder = modelBuilder;
	}
	
	public void setPostProcessors(ModelPostProcessor[] postProcessors) {
		this.postProcessors = postProcessors;
	}
	
	public void setPostProcessor(ModelPostProcessor postProcessor) {
		this.postProcessors = new ModelPostProcessor[] { postProcessor };
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setAddUriToCacheKey(boolean addUriToCacheKey) {
		this.addUriToCacheKey = addUriToCacheKey;
	}
	
	protected void initController() {	
		if (viewName == null) {
			throw new BeanCreationException(getBeanName() 
					+ ": A viewName must be set.");
		}
		if (modelBuilder == null) {
			throw new BeanCreationException(getBeanName() 
					+ ": A ModelBuilder must be set.");
		}
		if (modelBuilder instanceof CacheableModelBuilder) {
			cacheableModelBuilder = (CacheableModelBuilder) modelBuilder;
		}
		else {
			setCacheable(false);
		}
	}
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) {
			
		try {
			Map model = modelBuilder.buildModel(request);
	
			if (postProcessors != null) {
				for (int i = 0; i < postProcessors.length; i++) {
					postProcessors[i].postProcess(model, request);
				}
			}
			if (contentType != null) {
				response.setContentType(contentType);
			}
			return new ModelAndView(viewName, model);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public long getLastModified(HttpServletRequest request) {
		if (cacheableModelBuilder != null) {
			return cacheableModelBuilder.getLastModified(request); 
		}
		return -1;
	}
		
	protected void appendCacheKeyInternal(StringBuffer key, 
			HttpServletRequest request) {
		
		super.appendCacheKeyInternal(key, request);
		
		if (cacheableModelBuilder != null) {
			cacheableModelBuilder.appendCacheKey(key, request);
			if (addUriToCacheKey) {
				super.appendCacheKey(key, request);	
			}
		}
	}

}
