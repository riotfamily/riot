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
package org.riotfamily.pages.component.editor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.beans.propertyeditors.BooleanEditor;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.forms.controller.RepositoryFormController;
import org.riotfamily.forms.element.core.Checkbox;
import org.riotfamily.forms.element.core.FileUpload;
import org.riotfamily.forms.element.core.ImageUpload;
import org.riotfamily.forms.factory.FormDefinitionException;
import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.component.property.FileStoreProperyProcessor;
import org.riotfamily.pages.component.property.PropertyEditorProcessor;
import org.riotfamily.pages.setup.Plumber;
import org.riotfamily.pages.setup.WebsiteConfig;
import org.riotfamily.pages.setup.WebsiteConfigAware;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

public class ComponentFormController extends RepositoryFormController
		implements FormSubmissionHandler, ApplicationContextAware,
		WebsiteConfigAware, ConfigurationEventListener {

	private static final String SESSION_ATTRIBUTE = "componentForm";
	
	private static final String COMPONENT_ID = "componentId";
	
	private static final String INSTANT_PUBLISH_PARAM = "instantPublish";
	
	private ComponentDao componentDao;
	
	private PlatformTransactionManager transactionManager;
	
	private String viewName = ResourceUtils.getPath(
			ComponentFormController.class, "ComponentFormView.ftl");
	
	private String successViewName = ResourceUtils.getPath(
			ComponentFormController.class, "ComponentFormSuccessView.ftl");
	
	private ComponentRepository componentRepository;
	
	public ComponentFormController(FormRepository formRepository, 
			PlatformTransactionManager transactionManager) {
		
		super(formRepository);
		this.transactionManager = transactionManager;		
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.form.button.save");
		buttonFactory.setCssClass("button button-save");
		addButton(buttonFactory);
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		Plumber.register(applicationContext, this);
	}
		
	public void setWebsiteConfig(WebsiteConfig websiteConfig) {
		componentRepository = websiteConfig.getComponentRepository();
		componentDao = websiteConfig.getComponentDao();
		componentRepository.addListener(this);
		setupForms();
	}
	
	public void beanReconfigured(ConfigurableBean bean) {
		setupForms();
	}
	
	protected void setupForms() {
		Iterator it = getFormRepository().getFormIds().iterator();
		while (it.hasNext()) {
			String id = (String) it.next();
			Component component = componentRepository.getComponent(id);			
			try {
				setupForm(component, getFormRepository().createForm(id));
			}
			catch (FormDefinitionException e) {
			}
		}		
	}
	
	//TODO Refactor: Move this to a separate class.
	
	protected void setupForm(Component component, Form form) {
		componentRepository.addFormId(form.getId());
		Iterator it = form.getRegisteredElements().iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			if (e instanceof FileUpload) {
				FileUpload upload = (FileUpload) e;
				component.addPropertyProcessor(
						new FileStoreProperyProcessor(
						upload.getEditorBinding().getProperty(),
						upload.getFileStore()));
				
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
		}
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setSuccessViewName(String successViewName) {
		this.successViewName = successViewName;
	}
	
	protected ComponentVersion getPreview(HttpServletRequest request) {
		Long id = new Long((String) request.getAttribute(COMPONENT_ID));
		boolean instanPublishMode = ServletRequestUtils.getBooleanParameter(
				request, INSTANT_PUBLISH_PARAM, false);
		
		return componentDao.getComponentVersionForContainer(id, instanPublishMode);
	}
	
	public String getFormId(String componentType) {
		return componentRepository.getFormId(componentType);
	}
	
	protected String getFormId(HttpServletRequest request) {
		ComponentVersion preview = getPreview(request);
		return getFormId(preview.getType());
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

		return (ModelAndView) execInTransaction(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				StringWriter sw = new StringWriter();
				renderForm(form, new PrintWriter(sw));
				return new ModelAndView(viewName, "form", sw.toString());
			}
		});		
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
	
	protected void processAjaxRequest(final Form form, final HttpServletRequest request, 
					final HttpServletResponse response) throws IOException {
		
		execInTransaction(new TransactionCallbackWithoutResult() {
		
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				try {
					ComponentFormController.super.processAjaxRequest(form, request, response);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}		
		});		
	}
	
	protected Object execInTransaction(TransactionCallback callback) {
		return new TransactionTemplate(transactionManager).execute(callback);		
	}

}