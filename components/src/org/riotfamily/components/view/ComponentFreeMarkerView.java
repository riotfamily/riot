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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.view;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.web.util.DeferredRenderingResponseWrapper;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.common.web.view.freemarker.RiotFreeMarkerView;
import org.riotfamily.components.EditModeUtils;
import org.riotfamily.components.config.component.AbstractComponent;
import org.riotfamily.components.context.PageRequestUtils;
import org.riotfamily.components.context.StoreContextInterceptor;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentFreeMarkerView extends RiotFreeMarkerView {
	
	public static final String WRAP_OUTPUT_ATTRIBUTE = 
			ComponentFreeMarkerView.class.getName() + ".wrapOutput";
	
	private static final String DEFERRED = 
			ComponentFreeMarkerView.class.getName() + ".deferred";
	
	
	protected HttpServletResponse wrapResponse(HttpServletRequest request, 
			HttpServletResponse response) {
		
		if (EditModeUtils.isEditMode(request)
				&& !ServletUtils.isDirectRequest(request)
				&& !isComponentView(request)
				&& !PageRequestUtils.isPartialRequest(request)) {
		
			request.setAttribute(DEFERRED, Boolean.TRUE);
			return new DeferredRenderingResponseWrapper(response);
		}
		return response;
	}
	
	protected void renderMergedTemplateModel(Map model, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		// Check whether the response was wrapped by _this_ view. Must be done
		// before the view is rendered, since the view might include other views.
		boolean deferred = isDeferred(request);
		
		super.renderMergedTemplateModel(model, request, response);
		
		if (deferred) {
			
			DeferredRenderingResponseWrapper deferredResponse =
					(DeferredRenderingResponseWrapper) response;
			
			if (requiresWrapping(request)) {
				StoreContextInterceptor.storeContext(request);
				String path = ServletUtils.getPathWithinApplication(request);
				PrintWriter out = deferredResponse.getResponse().getWriter();
				TagWriter wrapper = new TagWriter(out);
				wrapper.start(Html.DIV)
					.attribute(Html.COMMON_CLASS, "riot-components")
					.attribute("riot:controllerId", path)
					.body();
			
				deferredResponse.renderResponse();
				wrapper.end();
			}
			else {
				deferredResponse.renderResponse();
			}
		}
	}
		
	private boolean isComponentView(HttpServletRequest request) {
		return request.getAttribute(AbstractComponent.CONTAINER) != null;
	}
	
	private boolean isDeferred(HttpServletRequest request) {
		boolean deferred = request.getAttribute(DEFERRED) == Boolean.TRUE;
		if (deferred) {
			request.removeAttribute(DEFERRED);
		}
		return deferred;
	}
	
	private boolean requiresWrapping(HttpServletRequest request) {
		boolean wrap = request.getAttribute(WRAP_OUTPUT_ATTRIBUTE) != null;
		if (wrap) {
			request.removeAttribute(WRAP_OUTPUT_ATTRIBUTE);
		}
		return wrap;
	}
	
}
