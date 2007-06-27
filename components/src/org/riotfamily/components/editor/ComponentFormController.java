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
package org.riotfamily.components.editor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.common.web.transaction.TransactionalController;
import org.riotfamily.components.Component;
import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.ComponentVersion;
import org.riotfamily.components.VersionContainer;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.forms.controller.RepositoryFormController;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that displays a form to edit the properties of a ComponentVersion.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentFormController extends RepositoryFormController
		implements FormSubmissionHandler, TransactionalController,
		UrlMappingAware, BeanNameAware {

	private static final String SESSION_ATTRIBUTE = "componentForm";

	private ComponentDao componentDao;

	private String viewName = ResourceUtils.getPath(
			ComponentFormController.class, "ComponentFormView.ftl");

	private String successViewName = ResourceUtils.getPath(
			ComponentFormController.class, "ComponentFormSuccessView.ftl");

	private ComponentRepository componentRepository;

	private UrlMapping urlMapping;

	private String beanName;

	private String formIdAttribute = "formId";

	private String containerIdAttribute = "containerId";

	public ComponentFormController(FormRepository formRepository,
			ComponentRepository componentRepository,
			ComponentDao componentDao) {

		super(formRepository);
		this.componentRepository = componentRepository;
		this.componentDao = componentDao;
		componentRepository.setFormController(this);
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.form.button.save");
		buttonFactory.setCssClass("button button-save");
		addButton(buttonFactory);
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}

	public String getUrl(String formId, Long containerId) {
		FlatMap attributes = new FlatMap();
		attributes.put(formIdAttribute, formId);
		attributes.put(containerIdAttribute, containerId);
		return urlMapping.getUrl(beanName, attributes);
	}

	protected ComponentRepository getComponentRepository() {
		return this.componentRepository;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setSuccessViewName(String successViewName) {
		this.successViewName = successViewName;
	}

	protected ComponentVersion getVersion(HttpServletRequest request) {
		Long id = new Long((String) request.getAttribute(containerIdAttribute));
		VersionContainer container = componentDao.loadVersionContainer(id);
		boolean live = ServletRequestUtils.getBooleanParameter(request, "live", false);
		return componentDao.getOrCreateVersion(container, null, live);
	}

	protected String getFormId(HttpServletRequest request) {
		return (String) request.getAttribute(formIdAttribute);
	}

	protected Object getFormBackingObject(HttpServletRequest request) {
		ComponentVersion version = getVersion(request);
		Component component = componentRepository.getComponent(version);
		return component.buildModel(version);
	}

	protected String getSessionAttribute(HttpServletRequest request) {
		return SESSION_ATTRIBUTE;
	}

	protected ModelAndView showForm(final Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		StringWriter sw = new StringWriter();
		renderForm(form, new PrintWriter(sw));
		return new ModelAndView(viewName, "form", sw.toString());
	}

	public ModelAndView handleFormSubmission(Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ComponentVersion version = getVersion(request);
		Component component = componentRepository.getComponent(version);
		Map properties = (Map) form.populateBackingObject();
		component.updateProperties(version, properties);
		componentDao.updateComponentVersion(version);
		return new ModelAndView(successViewName);
	}

}