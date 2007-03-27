/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.mvc;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.website.mvc.cache.CacheableModelBuilder;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Generic controller implementation that utilizes a ModelBuilder to create
 * a model which is passed on to a view with a configurable name. Additionally
 * ModelPostProcessors can be registered to tweak the model.
 */
public class GenericController extends AbstractCacheableController {

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
	}
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
			
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

	protected boolean bypassCache(HttpServletRequest request) {
		return cacheableModelBuilder == null;
	}
	
	public long getLastModified(HttpServletRequest request) {
		return cacheableModelBuilder.getLastModified(request); 
	}
		
	protected void appendCacheKey(StringBuffer key,	
			HttpServletRequest request) {
		
		cacheableModelBuilder.appendCacheKey(key, request);
		if (addUriToCacheKey) {
			super.appendCacheKey(key, request);	
		}
	}

}
