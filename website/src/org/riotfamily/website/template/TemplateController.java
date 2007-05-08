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
 *   Alf Werder <alf.werder@glonz.com>
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller that passes a map of URLs to it's view (the template). The view
 * is responsible for including the URLs (using a RequestDispatcher) at the
 * right place.
 * <p>
 * The most simple way to achieve this is to use a JSTL view that contains
 * a <code>&lt;c:import value="${<i>slotname</i>}" /&gt;</code> tag for each
 * slot, where <code><i>slotname</i></code> has to be one of the keys present
 * in the controllers configuration map.
 * </p>
 * <p>
 * You may extend existing template configurations by setting the
 * {@link #setParent(TemplateController) parent} property to another
 * TemplateController. The local configuration will then be merged with the
 * one of the parent, overriding previously defined URLs.
 * </p>
 * <p>
 * Additionally the controller supports nested templates, i.e. the URL of a
 * slot may in turn map to another TemplateController. These nested structures
 * are taken into account when configurations are merged. When extending a
 * parent you may also override URLs defined in nested templates.
 * </p>
 * <p>
 * Let's say Template A has two slots, <i>left</i> and <i>right</i>. The right
 * slot includes another Template B which also has two slots <i>top</i> and
 * <i>bottom</i>, where <i>top</i> contains the URL <code>/foo.html</code>.
 * </p>
 * We can now define a third TemplateController A2 which extends A:
 * <pre>
 * &lt;template:definition name="A2" parent="A"&gt;
 *     &lt;template:insert slot="right.top" url="/bar.html" /&gt;
 * &lt;/template:definition&gt;
 * </pre>
 * <p>
 * The syntax above makes use of the Spring 2.0 namespace support. See
 * {@link org.riotfamily.website.template.config.TemplateNamespaceHandler}
 * for more information.
 * </p>
 *
 * @author Alf Werder
 * @author Felix Gnass
 */
public class TemplateController extends AbstractController
		implements InitializingBean {

	/** NOTE: The DispatcherServlet class name prefix forces an attribute
	 * cleanup to be performed after an include, regardless of the servlet's
	 * cleanupAfterIncludes setting.
	 */
	private static final String SLOTS_CONFIGURATION_ATTRIBUTE =
			DispatcherServlet.class.getName() + "#" +
			TemplateController.class.getName() + ".SLOTS_CONFIG";

	private static final String SLOT_PATH_ATTRIBUTE =
		DispatcherServlet.class.getName() + "#" +
		TemplateController.class.getName() + ".SLOT_PATH";

	private static final String SLOT_PARAMETER =
			TemplateController.class.getName() + ".SLOT";

	private TemplateController parent;

	private String viewName;

	private Map configuration;

	private Map mergedConfiguration;

	public TemplateController getParent() {
		return parent;
	}

	public void setParent(TemplateController parent) {
		this.parent = parent;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String view) {
		this.viewName = view;
	}

	public Map getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map configuration) {
		this.configuration = configuration;
	}

	/**
	 * Initializes the controller after all properties have been set. If a
	 * parent controller is set the configuration will be merged and the view
	 * will be inherited (if not set locally).
	 */
	public void afterPropertiesSet() throws Exception {
		mergeConfiguration();
		inheritView();
	}

	/**
	 * Merges the configuration map with the ones defined by ancestors.
	 */
	protected void mergeConfiguration() {
		mergedConfiguration = new HashMap();
		if (parent != null) {
			mergedConfiguration.putAll(parent.getMergedConfiguration());
		}
		if (configuration != null) {
			mergedConfiguration.putAll(configuration);
		}
	}

	protected Map getMergedConfiguration() {
		return mergedConfiguration;
	}

	/**
	 * Sets the view to the parent view if it has not been set locally.
	 */
	private void inheritView() {
		if (viewName == null && getParent() != null) {
			viewName = getParent().getViewName();
		}
	}

	private Map getEffectiveConfiguration(HttpServletRequest request) {
		Map effectiveConfiguration = new HashMap(mergedConfiguration);

		Map slotsConfiguration = (Map) request.getAttribute(
				SLOTS_CONFIGURATION_ATTRIBUTE);

		if (slotsConfiguration != null) {
			String slot = request.getParameter(SLOT_PARAMETER);
			if (slot != null) {
				String prefix = slot + '.';
				effectiveConfiguration.putAll(
						selectEntries(slotsConfiguration, prefix));
			}
		}

		return effectiveConfiguration;
	}

	/**
	 * Creates a new map containing all entries starting with the given prefix.
	 * The prefix is stripped from the keys of the new map.
	 */
	private static Map selectEntries(Map map, String prefix) {
		Map result = new HashMap();
		int prefixLength = prefix.length();
		Iterator i = map.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			String key = (String) entry.getKey();
			if (key.startsWith(prefix)) {
				result.put(key.substring(prefixLength), entry.getValue());
			}
		}
		return result;
	}

	/**
	 * Builds a map of URLs that is used as model for the template view.
	 */
	protected Map buildUrlMap(Map config) {
		Map model = new HashMap();
		Iterator i = config.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			String slot = (String) entry.getKey();
			if (slot.indexOf('.') == -1) {
				String location = (String) entry.getValue();
				if (location != null) {
					model.put(slot, getSlotUrl(location, slot));
				}
				else {
					model.remove(slot);
				}
			}
		}
		return model;
	}

	/**
	 * Returns the include URL for the given location and slot. By default
	 * <code>SLOT_REQUEST_PARAMETER_NAME</code> is appended, containing the
	 * given slot name.
	 */
	protected String getSlotUrl(String location, String slot) {
		StringBuffer url = new StringBuffer();
		url.append(location);
		url.append((url.indexOf("?") != -1) ? '&' : '?');
		url.append(SLOT_PARAMETER);
		url.append('=');
		url.append(slot);
		return url.toString();
	}

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map config = getEffectiveConfiguration(request);
		request.setAttribute(SLOTS_CONFIGURATION_ATTRIBUTE, config);
		request.setAttribute(SLOT_PATH_ATTRIBUTE, getSlotPath(request));
		if (getViewName() != null) {
			return new ModelAndView(getViewName(), buildUrlMap(config));
		}
		else {
			render(config, request, response);
			return null;
		}
	}

	protected void render(Map config, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ArrayList slots = new ArrayList(config.keySet());
		Collections.sort(slots);
		Iterator it = slots.iterator();
		while (it.hasNext()) {
			String slot = (String) it.next();
			if (slot.indexOf('.') == -1) {
				String location = (String) config.get(slot);
				if (location != null) {
					String url = getSlotUrl(location, slot);
					RequestDispatcher rd = request.getRequestDispatcher(url);
					rd.include(request, response);
				}
			}
		}
	}

	/**
	 * Returns the fully qualified slot-path for the given request.
	 */
	public static String getSlotPath(HttpServletRequest request) {
		String slotPath = (String) request.getAttribute(SLOT_PATH_ATTRIBUTE);
		String slot = request.getParameter(SLOT_PARAMETER);
		if (slot != null) {
			if (slotPath != null) {
				slotPath = slotPath + '.' + slot;
			}
			else {
				slotPath = slot;
			}
		}
		return slotPath;
	}

}