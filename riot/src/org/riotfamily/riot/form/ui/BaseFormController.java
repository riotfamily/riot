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
package org.riotfamily.riot.form.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.forms.controller.RepositoryFormController;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.FormDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 */
public abstract class BaseFormController extends RepositoryFormController 
		implements UrlMappingAware, BeanNameAware, FormSubmissionHandler {

	protected static final String FORM_DEFINITION_ATTR = 
			FormController.class.getName() + ".formDefinition";
	
	private EditorRepository editorRepository;
	
	private String editorIdAttribute = "editorId";
	
	private String objectIdAttribute = "objectId";
	
	private String parentIdParam = "parentId";
	
	private String viewName = ResourceUtils.getPath(
			BaseFormController.class, "FormView.ftl");
	
	private UrlMapping urlMapping;
	
	private String beanName;
	
	private PlatformTransactionManager transactionManager;
	
	public BaseFormController(EditorRepository editorRepository, 
			FormRepository formRepository, 
			PlatformTransactionManager transactionManager) {
		
		super(formRepository);
		this.editorRepository = editorRepository;
		this.transactionManager = transactionManager;
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.form.button.save");
		buttonFactory.setCssClass("button button-save");
		addButton(buttonFactory);
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
		
	public Class getDefinitionClass() {
		return FormDefinition.class;
	}

	public String getUrl(String editorId, String objectId, String parentId) {
		HashMap attrs = new HashMap();
		attrs.put(editorIdAttribute, editorId);
		if (objectId != null) {
			attrs.put(objectIdAttribute, objectId);
		}
		StringBuffer url = new StringBuffer(urlMapping.getUrl(beanName, attrs));
		if (parentId != null) {
			url.append('?').append(parentIdParam);
			url.append('=').append(parentId);
		}
		return url.toString();
	}

	protected String getSessionAttribute(HttpServletRequest request) {
		return BaseFormController.class.getName() 
				+ request.getAttribute(editorIdAttribute);
	}
	
	protected FormDefinition getFormDefinition(HttpServletRequest request) {
		FormDefinition formDefinition = (FormDefinition) 
				request.getAttribute(FORM_DEFINITION_ATTR);
		
		if (formDefinition == null) {
			String editorId = (String) request.getAttribute(editorIdAttribute);
			Assert.notNull(editorId, "An editorId attribute must be set");
			formDefinition = editorRepository.getFormDefinition(editorId);
			Assert.notNull(formDefinition, "No such editor: " + editorId);
			request.setAttribute(FORM_DEFINITION_ATTR, formDefinition);
		}
		return formDefinition;
	}
	
	protected String getFormId(HttpServletRequest request) {
		return getFormDefinition(request).getFormId();
	}
	
	protected String getObjectId(HttpServletRequest request) {
		return (String) request.getAttribute(objectIdAttribute);
	}
	
	protected String getParentId(HttpServletRequest request) {
		return request.getParameter(parentIdParam);
	}
	
	protected Form createForm(HttpServletRequest request) {
		Form form = super.createForm(request);
		FormUtils.setObjectId(form, getObjectId(request));
		FormUtils.setParentId(form, getParentId(request));
		return form;
	}
	
	protected Object execInTransaction(TransactionCallback callback) {
		return new TransactionTemplate(transactionManager).execute(callback);		
	}
	
	protected Form createAndInitForm(final HttpServletRequest request, 
			final HttpServletResponse response) throws Exception {
		
		return (Form) execInTransaction(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus ts) {
				try {
					return BaseFormController.super.createAndInitForm(
							request, response);		
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	protected ModelAndView handleFormRequest(final Form form, 
			final HttpServletRequest request, 
			final HttpServletResponse response) throws Exception {
		
		return (ModelAndView) execInTransaction(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus ts) {
				try {
					return BaseFormController.super.handleFormRequest(
							form, request, response);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	/**
	 * @see org.riotfamily.forms.controller.RepositoryFormController#getFormBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object getFormBackingObject(HttpServletRequest request) {
		final FormDefinition formDefinition = getFormDefinition(request);
		final String objectId = getObjectId(request);
		if (objectId == null) {
			return null;
		}
		return EditorDefinitionUtils.loadBean(formDefinition, objectId);
	}
	
	protected Map createModel(Form form, FormDefinition formDefinition, 
			HttpServletRequest request, HttpServletResponse response) {
		
		HashMap model = new HashMap();
		model.put("editorId", formDefinition.getId());
		model.put("parentId", FormUtils.getParentId(form));
		model.put("objectId", FormUtils.getObjectId(form));
		model.put("formId", form.getId());
		return model;
	}

	/**
	 * @see org.riotfamily.forms.controller.AbstractFormController#showForm(org.riotfamily.forms.Form, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected ModelAndView showForm(final Form form, 
			HttpServletRequest request, HttpServletResponse response) {
		
		final StringWriter sw = new StringWriter();
		execInTransaction(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus ts) {
				renderForm(form, new PrintWriter(sw));
			}
		});
		
		Map model = createModel(form, getFormDefinition(request), 
				request, response);
		
		model.put("form", sw.toString());
		return new ModelAndView(viewName, model);
	}

	
	public final ModelAndView handleFormSubmission(Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	
		Object bean = form.populateBackingObject();
		FormDefinition formDef = getFormDefinition(request);
		
		ListDefinition listDef = EditorDefinitionUtils.getParentListDefinition(formDef);
		RiotDao dao = listDef.getListConfig().getDao();
			
		if (form.isNew()) {
			log.debug("Saving entity ...");
			String parentId = FormUtils.getParentId(form);
			Object parent = EditorDefinitionUtils.loadParent(listDef, parentId);
			dao.save(bean, parent);
			FormUtils.setObjectId(form, dao.getObjectId(bean));
			form.setValue(bean);
			return afterSave(form, formDef, request, response);
		}
		else {
			log.debug("Updating entity ...");
			dao.update(bean);
			return afterUpdate(form, formDef, request, response);
		}
	}
	
	protected abstract ModelAndView afterSave(Form form, FormDefinition formDefinition, 
			HttpServletRequest request, HttpServletResponse response);
	
	protected abstract ModelAndView afterUpdate(Form form, FormDefinition formDefinition, 
			HttpServletRequest request, HttpServletResponse response);
	
}
