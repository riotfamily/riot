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
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.beans.propertyeditors.BooleanEditor;
import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.common.web.transaction.TransactionalController;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.components.Component;
import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.ComponentVersion;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.property.FileStoreProperyProcessor;
import org.riotfamily.components.property.PropertyEditorProcessor;
import org.riotfamily.components.property.XmlPropertyProcessor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.forms.controller.RepositoryFormController;
import org.riotfamily.forms.element.core.Checkbox;
import org.riotfamily.forms.element.core.FileUpload;
import org.riotfamily.forms.element.core.ImageUpload;
import org.riotfamily.forms.element.core.NumberField;
import org.riotfamily.forms.element.dom.XmlSequence;
import org.riotfamily.forms.factory.FormDefinitionException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.servlet.ModelAndView;

public class ComponentFormController extends RepositoryFormController
		implements FormSubmissionHandler, ConfigurationEventListener,
		TransactionalController, UrlMappingAware, BeanNameAware {

	private static final String SESSION_ATTRIBUTE = "componentForm";

	private ComponentDao componentDao;

	private String viewName = ResourceUtils.getPath(
			ComponentFormController.class, "ComponentFormView.ftl");

	private String successViewName = ResourceUtils.getPath(
			ComponentFormController.class, "ComponentFormSuccessView.ftl");

	private ComponentRepository componentRepository;

	private ComponentFormRegistry formRegistry;

	private UrlMapping urlMapping;

	private String beanName;

	private String formIdAttribute = "formId";

	private String containerIdAttribute = "containerId";

	public ComponentFormController(FormRepository formRepository,
			ComponentRepository componentRepository,
			ComponentFormRegistry formRegistry, ComponentDao componentDao) {

		super(formRepository);
		this.componentRepository = componentRepository;
		this.formRegistry = formRegistry;
		this.componentDao = componentDao;
		componentRepository.addListener(this);
		formRepository.addListener(this);
		formRegistry.setFormController(this);
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.form.button.save");
		buttonFactory.setCssClass("button button-save");
		addButton(buttonFactory);
		formRegistry.clear();
		setupForms();
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

	public void beanReconfigured(ConfigurableBean bean) {
		formRegistry.clear();
		setupForms();
	}

	protected void setupForms() {
		Iterator it = getFormRepository().getFormIds().iterator();
		while (it.hasNext()) {
			String id = (String) it.next();
			Component component = componentRepository.getComponent(id);
			componentRepository.resetPropertyProcessors(component);
			try {
				setupForm(component, getFormRepository().createForm(id));
			}
			catch (FormDefinitionException e) {
			}
		}
	}

	//TODO Refactor: Move this to a separate class.

	protected void setupForm(Component component, Form form) {
		formRegistry.registerFormId(form.getId());
		Iterator it = form.getRegisteredElements().iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			if (e instanceof FileUpload) {
				FileUpload upload = (FileUpload) e;
				component.addPropertyProcessor(
						new FileStoreProperyProcessor(
						upload.getEditorBinding().getProperty(),
						upload.getFileStore()));

				if (upload.getSizeProperty() != null) {
					component.addPropertyProcessor(
							new PropertyEditorProcessor(
							upload.getSizeProperty(),
							new CustomNumberEditor(Long.class, true)));
				}
				if (upload instanceof ImageUpload) {
					ImageUpload imageUpload = (ImageUpload) upload;
					if (imageUpload.getWidthProperty() != null) {
						component.addPropertyProcessor(
								new PropertyEditorProcessor(
								imageUpload.getWidthProperty(),
								new CustomNumberEditor(Integer.class, true)));
					}
					if (imageUpload.getHeightProperty() != null) {
						component.addPropertyProcessor(
								new PropertyEditorProcessor(
								imageUpload.getHeightProperty(),
								new CustomNumberEditor(Integer.class, true)));
					}
				}
			}
			else if (e instanceof Checkbox) {
				Checkbox cb = (Checkbox) e;
				component.addPropertyProcessor(
						new PropertyEditorProcessor(
						cb.getEditorBinding().getProperty(),
						new BooleanEditor(),
						Boolean.toString(cb.isCheckedByDefault())));
			}
			else if (e instanceof NumberField) {
				NumberField nf = (NumberField) e;
				Class numberClass = nf.getEditorBinding().getPropertyType();
				if (!(Number.class.isAssignableFrom(numberClass))) {
					numberClass = Integer.class;
				}
				component.addPropertyProcessor(
						new PropertyEditorProcessor(
						nf.getEditorBinding().getProperty(),
						new CustomNumberEditor(numberClass, false)));
			}
			else if (e instanceof XmlSequence) {
				XmlSequence xs = (XmlSequence) e;
				component.addPropertyProcessor(new XmlPropertyProcessor(
						xs.getEditorBinding().getProperty()));
			}
		}
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setSuccessViewName(String successViewName) {
		this.successViewName = successViewName;
	}

	protected ComponentVersion getPreview(HttpServletRequest request) {
		Long id = new Long((String) request.getAttribute(containerIdAttribute));
		return componentDao.getComponentVersionForContainer(id, false);
	}

	protected String getFormId(HttpServletRequest request) {
		return (String) request.getAttribute(formIdAttribute);
	}

	protected Object getFormBackingObject(HttpServletRequest request) {
		ComponentVersion preview = getPreview(request);
		Component component = componentRepository.getComponent(preview);
		return component.buildModel(preview);
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

		ComponentVersion preview = getPreview(request);
		Component component = componentRepository.getComponent(preview);
		Map properties = (Map) form.populateBackingObject();
		component.updateProperties(preview, properties);
		componentDao.updateComponentVersion(preview);
		return new ModelAndView(successViewName);
	}

}