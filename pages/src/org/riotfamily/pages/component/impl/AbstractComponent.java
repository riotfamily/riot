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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.property.PropertyProcessor;
import org.springframework.util.Assert;

/**
 * Abstract base class for component implementations. 
 */
public abstract class AbstractComponent implements Component {

	public static final String CONTAINER = AbstractComponent.class.getName() 
			+ ".container";
	
	public static final String COMPONENT_ID = "componentId";
	
	public static final String POSITION_CLASS = "positionClass";
	
	protected Log log = LogFactory.getLog(AbstractComponent.class);
	
	private String description;
	
	private List propertyProcessors = new ArrayList();
	
	
	public void setPropertyProcessors(List propertyProcessors) {
		Assert.notNull(propertyProcessors);
		this.propertyProcessors = propertyProcessors;
	}

	public void addPropertyProcessor(PropertyProcessor propertyProcessor) {
		propertyProcessors.add(propertyProcessor);
	}
	
	public List getPropertyProcessors() {		
		return propertyProcessors;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String name) {
		this.description = name;
	}
	
	
	public final void render(ComponentVersion componentVersion, 
			String positionClassName, HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		
		try {
			request.setAttribute(CONTAINER, componentVersion.getContainer());
			renderInternal(componentVersion, positionClassName, request, response);
		}
		catch (Exception e) {
			log.error("Error rendering component", e);
			
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			StringWriter strace = new StringWriter();
			e.printStackTrace(new PrintWriter(strace));
			
			TagWriter pre = new TagWriter(response.getWriter());
			pre.start("pre")
					.attribute("class", "riot-stacktrace")
					.body(strace.toString());
			
			pre.end();
		}
	}
	
	public boolean isDynamic() {
		return false;
	}
	
	public Map buildModel(ComponentVersion componentVersion) {
		Map model = new HashMap();
		model.putAll(componentVersion.getProperties());
		Iterator it = propertyProcessors.iterator();
		while (it.hasNext()) {
			PropertyProcessor pp = (PropertyProcessor) it.next();
			pp.resolveStrings(model);
		}
		return model;
	}
	
	public void updateProperties(ComponentVersion version, Map model) {
		Iterator it = propertyProcessors.iterator();
		while (it.hasNext()) {
			PropertyProcessor pp = (PropertyProcessor) it.next();
			pp.convertToStrings(model);
		}
		
		Map props = version.getProperties();
		
		it = model.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Assert.isInstanceOf(String.class, entry.getKey(), 
					"Map must only contain String keys.");
			
			if (entry.getValue() == null) {
				props.remove(entry.getKey());
			}
			else {
				Assert.isInstanceOf(String.class, entry.getValue(), 
						"Map must only contain String values.");
				
				props.put(entry.getKey(), entry.getValue());
			}
		}
		version.setDirty(true);
	}	
	
	protected abstract void renderInternal(ComponentVersion componentVersion, 
			String positionClassName, HttpServletRequest request, 
			HttpServletResponse response) throws Exception;

}
