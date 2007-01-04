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
import org.riotfamily.riot.editor.DisplayDefinition;
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

	public ListSession getOrCreateListSession(String editorId, String parentId, 
			String choose, HttpServletRequest request) {
		
		String key = "list-" + editorId + "#" + parentId;
		if (choose != null) {
			key += "-choose:" + choose;
		}
		
		ListSession session = getListSession(key, request);
		if (session == null) {
			ListDefinition listDef = editorRepository.getListDefinition(editorId);
			MessageResolver messageResolver = new MessageResolver(messageSource, 
					messageCodesResolver, RequestContextUtils.getLocale(request));
			
			session = new ListSession(key, listDef, parentId, messageResolver, 
					request.getContextPath(), editorRepository.getFormRepository(),
					formContextFactory);
			
			if (choose != null) {
				session.setChooserTarget((DisplayDefinition) 
						editorRepository.getEditorDefinition(choose)); 
			}
			
			request.getSession().setAttribute(key, session);
		}
		return session;
	}
	
	protected ListSession getListSession(String key, HttpServletRequest request) {
		return (ListSession) request.getSession().getAttribute(key);
	}
	
	public ListModel getModel(String key,HttpServletRequest request) {
		return getListSession(key, request).getModel(request);
	}
		
	public String getFilterFormHtml(String key,	HttpServletRequest request) {
		return getListSession(key, request).getFilterFormHtml();
	}

	public List getListCommands(String key,	HttpServletRequest request) {
		return getListSession(key, request).getListCommands(request);
	}
	
	public List getFormCommands(String key, String objectId, 
			HttpServletRequest request) {
		
		return getListSession(key, request).getFormCommands(objectId, request);
	}
	
	public CommandResult execCommand(String key, ListItem item, 
			String commandId, boolean confirmed, 
			HttpServletRequest request, HttpServletResponse response) {
		
		return getListSession(key, request).execCommand(
				item, commandId, confirmed, request, response);
	}

	public ListModel filter(String key, Map filter, HttpServletRequest request) {
		return getListSession(key, request).filter(filter, request);
	}

	public ListModel gotoPage(String key, int page, HttpServletRequest request) {
		return getListSession(key, request).gotoPage(page, request);
	}

	public ListModel sort(String key, String property, 
			HttpServletRequest request) {
		
		return getListSession(key, request).sort(property, request);
	}
}
