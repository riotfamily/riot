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
package org.riotfamily.components.config;

import java.util.HashMap;
import java.util.Map;

import org.riotfamily.components.model.Component;
import org.riotfamily.components.render.component.ComponentRenderer;
import org.riotfamily.components.render.component.ViewComponent;
import org.riotfamily.forms.factory.FormRepository;

/**
 * Repository containing component implementations.
 */
public class ComponentRepository {

	private String viewNamePrefix;

	private String viewNameSuffix;

	private FormRepository formRepository;
	
	private Map<String, ComponentRenderer> renderers = 
			new HashMap<String, ComponentRenderer>();

	public void setFormRepository(FormRepository formRepository) {
		this.formRepository = formRepository;
	}
	
	public void setRenderers(Map<String, ComponentRenderer> renderers) {
		this.renderers.putAll(renderers);
	}

	public ComponentRenderer getRenderer(String type) {
		ComponentRenderer renderer = renderers.get(type);
		if (renderer == null) {
			ViewComponent viewComponent = new ViewComponent();
			String viewName = viewNamePrefix + type + viewNameSuffix;
			viewComponent.setViewName(viewName);
			renderer = viewComponent;
			renderers.put(type, viewComponent);
		}
		return renderer;
	}

	public ComponentRenderer getRenderer(Component component) {
		return getRenderer(component.getType());
	}

	public Component createComponent(String type, Map<String, ?> properties) {
		Component component = new Component(type);
		ComponentRenderer renderer = getRenderer(type);
		Map<String, Object> values = new HashMap<String, Object>();
		if (renderer.getDefaults() != null) {
			values.putAll(renderer.getDefaults());
		}
		if (properties != null) {
			values.putAll(properties);
		}
		component.wrapValues(values);
		return component;
	}
	
	public void setViewNamePrefix(String defaultViewLocation) {
		this.viewNamePrefix = defaultViewLocation;
	}

	public void setViewNameSuffix(String viewSuffix) {
		this.viewNameSuffix = viewSuffix;
	}

	public String getFormUrl(String formId, Long containerId, Long componentId) {
		if (formRepository.containsForm(formId)) {
			return "/components/form/" + formId + "/" + containerId + "/" + componentId;
		}
		return null;
	}

}
