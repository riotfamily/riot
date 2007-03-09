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
package org.riotfamily.riot.list.command.dialog.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.common.web.transaction.TransactionalController;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.ajax.AjaxFormController;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.list.command.dialog.DialogCommand;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class DialogFormController extends AjaxFormController
		implements UrlMappingAware, BeanNameAware, FormSubmissionHandler,
		TransactionalController {
	
	private ListRepository listRepository;
	
	private UrlMapping urlMapping;
	
	private String beanName;
	
	private String commandIdAttribute = "commandId";
	
	private String viewName = ResourceUtils.getPath(
			DialogFormController.class, "DialogFormView.ftl");
	
	public DialogFormController(ListRepository listRepository) {
		this.listRepository = listRepository;
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.dialog.button.execute");
		buttonFactory.setCssClass("button button-execute");
		addButton(buttonFactory);
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}
		
	protected DialogCommand getCommand(HttpServletRequest request) {
		String commandId = (String) request.getAttribute(commandIdAttribute);
		return (DialogCommand) listRepository.getCommand(commandId);
	}
	
	/**
	 * Delegates the form creation to the DialogCommand.
	 */
	protected Form createForm(HttpServletRequest request) {
		return getForm(request);
	}
	
	/**
	 * Overwrites the super implementation to do nothing since the form
	 * created by the command is already populated by contract.
	 */
	protected void populateForm(Form form, HttpServletRequest request) {
	}
	
	protected String getSessionAttribute(HttpServletRequest request) {
		return getCommand(request).getFormSessionAttribute();
	}
	
	protected ModelAndView showForm(final Form form, 
			HttpServletRequest request, HttpServletResponse response) {
		
		StringWriter sw = new StringWriter();
		//REVISIT Do in transaction?
		renderForm(form, new PrintWriter(sw));
		
		Map model = new FlatMap();
		model.put("form", sw.toString());
		model.put("title", "FIXME");
		return new ModelAndView(viewName, model);
	}

	
	public ModelAndView handleFormSubmission(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		Object input = form.populateBackingObject();
		return getCommand(request).handleInput(input);
	}
	
	public String getUrl(String editorId, String objectId, String commandId) {
		return urlMapping.getUrl(beanName, Collections.singletonMap(
				commandIdAttribute, commandId));
	}
	
}
