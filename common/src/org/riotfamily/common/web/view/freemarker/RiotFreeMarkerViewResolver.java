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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.view.MacroHelperFactory;
import org.riotfamily.common.web.view.ModelPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

public class RiotFreeMarkerViewResolver extends FreeMarkerViewResolver {

	private boolean allowModelOverride = true;
	
	private boolean autoExposeMacroHelpers = true;
	
	private boolean freeMarkerServletMode = false;
	
	private List<ModelPostProcessor> modelPostProcessors = Generics.newArrayList();
	
	public RiotFreeMarkerViewResolver() {
		setExposeSpringMacroHelpers(true);
	}

	public void setAllowModelOverride(boolean allowModelOverride) {
		this.allowModelOverride = allowModelOverride;
	}

	public void setFreeMarkerServletMode(boolean freeMarkerServletMode) {
		this.freeMarkerServletMode = freeMarkerServletMode;
	}
	
	protected void initApplicationContext() {
		super.initApplicationContext();
		if (autoExposeMacroHelpers) {
			modelPostProcessors.add(new MacroHelperExposer(getApplicationContext()));	
		}
		modelPostProcessors.addAll(SpringUtils.listBeansOfType(
				getApplicationContext(), ModelPostProcessor.class));
	}

	protected Class<?> requiredViewClass() {
		return RiotFreeMarkerView.class;
	}
	
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		RiotFreeMarkerView view = (RiotFreeMarkerView) super.buildView(viewName);
		view.setAllowModelOverride(allowModelOverride);
		view.setFreeMarkerServletMode(freeMarkerServletMode);
		view.setModelPostProcessors(modelPostProcessors);
		return view;
	}
	
	private static class MacroHelperExposer implements ModelPostProcessor {
	
		private Map<String, MacroHelperFactory> macroHelperFactories;
		
		public MacroHelperExposer(ApplicationContext ctx) {
			macroHelperFactories = SpringUtils.beansOfType(ctx, MacroHelperFactory.class);
		}
		
		public void postProcess(Map<String, Object> model, 
				HttpServletRequest request, HttpServletResponse response) {
			
			for (Entry<String, MacroHelperFactory> entry : macroHelperFactories.entrySet()) {
				model.put(entry.getKey(), entry.getValue().createMacroHelper(request, response, model));
			}
		}
	}
}
