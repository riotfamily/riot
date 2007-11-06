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
package org.riotfamily.website.generic;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.website.generic.model.ModelBuilder;
import org.riotfamily.website.generic.model.ModelBuilderStack;
import org.springframework.web.servlet.ModelAndView;

/**
 * Generic controller implementation that utilizes a ModelBuilder to create
 * a model which is passed on to a view with a configurable name. Additionally
 * ModelPostProcessors can be registered to tweak the model.
 */
public class GenericController extends AbstractCacheableController {

	private ModelBuilderStack modelBuilderStack = new ModelBuilderStack();

	/** View name to use */
	private String viewName;

	private String contentType;

	/** Controls whether caching is being used or not */
	private boolean bypassCache = false;

	public GenericController() {
	}

	public GenericController(ModelBuilder[] modelBuilders) {
		setModelBuilders(modelBuilders);
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setCache(boolean cache) {
		this.bypassCache = !cache;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map model = modelBuilderStack.buildModel(request);

		if (contentType != null) {
			response.setContentType(contentType);
		}
		return new ModelAndView(viewName, model);
	}

	protected boolean bypassCache(HttpServletRequest request) {
		return bypassCache || !modelBuilderStack.isCacheable();
	}

	public long getTimeToLive() {
		return modelBuilderStack.getTimeToLive();
	}

	public long getLastModified(HttpServletRequest request) {
		return modelBuilderStack.getLastModified(request);
	}

	protected void appendCacheKey(StringBuffer key,	HttpServletRequest request) {
		modelBuilderStack.appendCacheKey(key, request);
	}

	public void setModelBuilders(ModelBuilder[] modelBuilders) {
		modelBuilderStack.setModelBuilders(modelBuilders);
	}
}
