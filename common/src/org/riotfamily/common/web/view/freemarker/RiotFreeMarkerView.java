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
package org.riotfamily.common.web.view.freemarker;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.collection.GuardedMap;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.common.web.util.RequestHolder;
import org.riotfamily.common.web.view.ModelPostProcessor;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import freemarker.ext.servlet.FreemarkerServlet;

/**
 * Differences to Spring's FreeMarkerView:
 * <ul>
 * <li>Model attributes may override attributes from the request or the session</li>
 * <li>The plain HttpServletRequest object is exposed under the key "request"</li>
 * <li>The model is not exposed to the request, unless the 
 * {@link #setFreeMarkerServletMode(boolean) freeMarkeServletMode} is enabled</li>
 * <li>Provides the possibility to post process the model via the 
 * {@link ModelPostProcessor} interface</li>
 * </ul> 
 */
public class RiotFreeMarkerView extends FreeMarkerView {	
	
	public static final String REQUEST_KEY = "request";
	
	public static final String MODEL_ATTRIBUTE = 
			RiotFreeMarkerView.class.getName() + ".model";

	private RiotLog log = RiotLog.get(RiotFreeMarkerView.class);
	
	private boolean allowModelOverride = true;
	
	private boolean freeMarkerServletMode = false;

	private Collection<ModelPostProcessor> modelPostProcessors;

	/**
	 * Sets whether the model may contain keys that are also present as request
	 * or session attributes. Otherwise an exception will be thrown when Spring
	 * tries to expose an attribute that conflicts with a key in the model. 	   
	 */
	public void setAllowModelOverride(boolean allowModelOverride) {
		this.allowModelOverride = allowModelOverride;
	}
	
	/**
	 * Sets whether the view should mimic the behavior of the 
	 * {@link FreemarkerServlet}, i.e. provide support JSP tag libraries, the
	 * "Request", "Session" and "Application" HashModels. By default this 
	 * mode is disabled, mainly because JSP support requires exposure of the
	 * model as separate request attributes, which can be quite confusing when
	 * working with nested views.
	 * 
	 * @param freeMarkerServletMode
	 */
	public void setFreeMarkerServletMode(boolean freeMarkerServletMode) {
		this.freeMarkerServletMode = freeMarkerServletMode;
	}
	
	public void setModelPostProcessors(Collection<ModelPostProcessor> modelPostProcessors) {
		this.modelPostProcessors = modelPostProcessors;
	}

	@SuppressWarnings("unchecked")
	public final void render(Map model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
	
		if (allowModelOverride) {
			Map<String, Object> emptyModel = Generics.newHashMap();
			emptyModel.put(MODEL_ATTRIBUTE, model);
			model = emptyModel;
		}
		super.render(model, request, wrapResponse(request, response));
	}

	/**
	 * Subclasses may override this method in order to wrap the 
	 * HttpServletResponse before it is passed on to the render method.
	 * The default implementation simply returns the given response. 
	 */
	protected HttpServletResponse wrapResponse(HttpServletRequest request, 
			HttpServletResponse response) {
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private void unwrapModel(Map model) {
		Map originalModel = (Map) model.remove(MODEL_ATTRIBUTE);
		if (originalModel != null) {
			model.putAll(originalModel);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void renderMergedTemplateModel(final Map model, 
			final HttpServletRequest request, 
			final HttpServletResponse response) 
			throws Exception {

		try {
			RequestHolder.set(request, response);
			unwrapModel(model);
			model.put(REQUEST_KEY, request);
			if (modelPostProcessors != null) {
				Map<String, Object> guardedModel = GuardedMap.guard(model);
				for (ModelPostProcessor processor : modelPostProcessors) {
					processor.postProcess(guardedModel, request, response);
				}
			}
			if (freeMarkerServletMode) {
				super.doRender(model, request, response);
			}
			else {
				if (log.isDebugEnabled()) {
					log.debug("Rendering FreeMarker template [" + getUrl() 
							+ "] in FreeMarkerView '" + getBeanName() + "'");
				}
				Locale locale = RequestContextUtils.getLocale(request);
				processTemplate(getTemplate(locale), model, response);
			}
		}
		finally {
			RequestHolder.unset();
		}
	}

}
