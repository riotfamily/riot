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
package org.riotfamily.riot.list.ui;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.command.CommandResult;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * @since 6.4
 */
public class ListServiceImpl implements ListService, MessageSourceAware {

	private EditorRepository editorRepository;
	
	private MessageSource messageSource;
	
	private AdvancedMessageCodesResolver messageCodesResolver;
	
	private FormContextFactory formContextFactory;
	
	public void setMessageCodesResolver(AdvancedMessageCodesResolver codesResolver) {
		this.messageCodesResolver = codesResolver;
	}

	public void setEditorRepository(EditorRepository editorRepository) {
		this.editorRepository = editorRepository;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setFormContextFactory(FormContextFactory formContextFactory) {
		this.formContextFactory = formContextFactory;
	}

	protected ListSession getListSession(String editorId, String parentId, 
			HttpServletRequest request) {
		
		String key = "list-" + editorId + "#" + parentId;
		ListSession session = (ListSession) request.getSession().getAttribute(key);
		if (session == null) {
			session = createListSession(editorId, parentId, request);
			request.getSession().setAttribute(key, session);
		}
		return session;
	}
	
	protected ListSession createListSession(String editorId, String parentId,
			HttpServletRequest request) {
		
		ListDefinition listDef = editorRepository.getListDefinition(editorId);
		MessageResolver messageResolver = new MessageResolver(messageSource, 
				messageCodesResolver, RequestContextUtils.getLocale(request));
		
		return new ListSession(listDef, parentId, messageResolver, 
				request.getContextPath(), editorRepository.getFormRepository(),
				formContextFactory);
	}
	
	public ListTable getTable(String editorId, String parentId,
			HttpServletRequest request) {
		
		return getListSession(editorId, parentId, request).getTable(request);
	}
		
	public String getFilterForm(String editorId, String parentId,
			HttpServletRequest request) {
		
		return getListSession(editorId, parentId, request).getFilterForm();
	}

	public List getListCommands(String editorId, String parentId, 
			HttpServletRequest request) {
		
		return getListSession(editorId, parentId, request)
				.getListCommands(request);
	}
	
	public List getFormCommands(String editorId, String parentId, 
			String objectId, HttpServletRequest request) {
		
		return getListSession(editorId, parentId, request).getFormCommands(
				objectId, request);
	}
	
	public CommandResult execCommand(String editorId, String parentId, 
			ListItem item, String commandId, boolean confirmed, 
			HttpServletRequest request, HttpServletResponse response) {
		
		return getListSession(editorId, parentId, request).execCommand(
				item, commandId, confirmed, request, response);
	}

	public List filter(String editorId, String parentId, Map filter, 
			HttpServletRequest request) {
		
		return getListSession(editorId, parentId, request).filter(
				filter, request);
	}

	public List getItems(String editorId, String parentId, 
			HttpServletRequest request) {
		
		return getListSession(editorId, parentId, request).getItems(request);
	}

	public List gotoPage(String editorId, String parentId, int page, 
			HttpServletRequest request) {
		
		return getListSession(editorId, parentId, request).gotoPage(
				page, request);
	}

	public ListTable sort(String editorId, String parentId, String property, 
			HttpServletRequest request) {
		
		return getListSession(editorId, parentId, request).sort(
				property, request);
	}
}
