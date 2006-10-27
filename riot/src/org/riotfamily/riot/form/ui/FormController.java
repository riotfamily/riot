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
package org.riotfamily.riot.form.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.FormDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ui.EditorController;
import org.riotfamily.riot.form.command.FormCommand;
import org.riotfamily.riot.form.command.FormCommandContext;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.support.CommandExecutor;
import org.riotfamily.riot.list.ui.render.CommandRenderer;
import org.riotfamily.riot.security.AccessController;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 */
public class FormController extends BaseFormController 
		implements EditorController, BeanNameAware {
	
	private ListRepository listRepository;
	
	private CommandExecutor commandExecutor;
	
	public FormController(EditorRepository editorRepository, 
			FormRepository formRepository, 
			PlatformTransactionManager transactionManager,
			CommandExecutor commandExecutor, ListRepository listRepository) {
		
		super(editorRepository, formRepository, transactionManager);
		this.commandExecutor = commandExecutor;
		this.listRepository = listRepository;
	}
		
	public Class getDefinitionClass() {
		return FormDefinition.class;
	}
		
	protected Command getCommand(HttpServletRequest request) {
		String commandId = request.getParameter("command");
		if (commandId == null) {
			return null;
		}
		return listRepository.getCommand(commandId);
	}
	
	protected boolean isCommandRequest(HttpServletRequest request) {
		return request.getParameter("command") != null;
	}
	
	/**
	 * Returns whether the given request is an initial form request.
	 */
	protected boolean isInitialRequest(HttpServletRequest request) {
		return super.isInitialRequest(request) && !isCommandRequest(request);
	}
	
	protected ModelAndView handleFormRequest(final Form form, 
			final HttpServletRequest request, 
			final HttpServletResponse response) throws Exception {

		final ModelAndView mv = super.handleFormRequest(form, request, response);
		if (isCommandRequest(request) && !form.hasErrors()) {
			execInTransaction(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus ts) {
					FormDefinition formDefinition = getFormDefinition(request);
					commandExecutor.executeCommand(listRepository, 
							formDefinition, form, form.getBackingObject(), 
							request, response, mv.getModel());
				}
			});
		}
		return mv;
	}

	
	protected Map createModel(Form form, FormDefinition formDefinition, 
			HttpServletRequest request, HttpServletResponse response) {		
		
		Map model = super.createModel(form, formDefinition, request, response);

		Object object = null;
		if (!form.isNew()) {
			object = form.getBackingObject();
		}
		try {
			if (!AccessController.isGranted(ACTION_VIEW, object, formDefinition)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		}
		catch (IOException e) {
			log.error("Error sending forbidden error for formDefinition[ " 
					+ formDefinition.getName() + " ]");
		}
		
		model.put("childLists", formDefinition.getChildEditorReferences(object, 
				form.getFormContext().getMessageResolver()));
				
		ListDefinition parentList = EditorDefinitionUtils
				.getParentListDefinition(formDefinition);
		
		if (parentList != null && listRepository != null) {
			ListConfig listConfig = listRepository.getListConfig(
					parentList.getListId());
			
			FormCommandContext context = new FormCommandContext(
					formDefinition, form, listConfig, object, 
					request, response);
			
			EditorDefinition parent = formDefinition.getParentEditorDefinition();
			
			if (!(parent instanceof FormDefinition)) {
				ArrayList commands = new ArrayList();
				CommandRenderer renderer = new CommandRenderer();
				renderer.setRenderText(true);
				
				Iterator it = listConfig.getColumnCommands().iterator();
				while (it.hasNext()) {
					Command command = (Command) it.next();
					if (command instanceof FormCommand) {
						context.setCommand(command);
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						renderer.render(context, pw);
						commands.add(sw.toString());
					}
				}
				model.put("commands", commands);
			}
		}
		
		return model;
	}
	
	protected ModelAndView afterSave(Form form, FormDefinition formDefinition, 
			HttpServletRequest request, HttpServletResponse response) {
	
		if (!formDefinition.getChildEditorDefinitions().isEmpty()) {
			return reloadForm(form, formDefinition);
		}
		else {
			return showParentList(form, formDefinition);
		}
	}
	
	protected ModelAndView afterUpdate(Form form, FormDefinition formDefinition, 
			HttpServletRequest request, HttpServletResponse response) {
	
		return showParentList(form, formDefinition);
	}

	protected ModelAndView showParentList(Form form, 
			FormDefinition formDefinition) {
		
		String listUrl = formDefinition.createEditorPath(
				form.getBackingObject(), 
				form.getFormContext().getMessageResolver())
				.getParent().getEditorUrl();
		
		return new ModelAndView(new RedirectView(listUrl, true));
	}
	
	protected ModelAndView reloadForm(Form form, 
			FormDefinition formDefinition) {
		
		String formUrl = formDefinition.createEditorPath(
				form.getBackingObject(), 
				form.getFormContext().getMessageResolver())
				.getEditorUrl();
		
		return new ModelAndView(new RedirectView(formUrl, true));
	}
}
