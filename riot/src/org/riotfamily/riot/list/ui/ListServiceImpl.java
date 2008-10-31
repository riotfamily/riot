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
package org.riotfamily.riot.list.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.util.SessionReferenceRemover;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.command.CommandResult;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ListServiceImpl implements ListService, MessageSourceAware,
		ConfigurationEventListener {

	private RiotLog log = RiotLog.get(ListServiceImpl.class);

	private EditorRepository editorRepository;

	private MessageSource messageSource;

	private AdvancedMessageCodesResolver messageCodesResolver;

	private FormContextFactory formContextFactory;
	
	private PlatformTransactionManager transactionManager;

	private Collection<ListSession> sessions = Generics.newArrayList();
	
	public void setMessageCodesResolver(AdvancedMessageCodesResolver codesResolver) {
		this.messageCodesResolver = codesResolver;
	}

	public void setEditorRepository(EditorRepository editorRepository) {
		this.editorRepository = editorRepository;
		editorRepository.addListener(this);
		editorRepository.getListRepository().addListener(this);
		editorRepository.getFormRepository().addListener(this);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setFormContextFactory(FormContextFactory formContextFactory) {
		this.formContextFactory = formContextFactory;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public ListSession getOrCreateListSession(String editorId, String parentId,
			String parentEditorId, String choose, HttpServletRequest request) {

		String key = "list-" + editorId + "_" + parentId;
		if (choose != null) {
			key += "-choose:" + choose;
		}

		ListSession listSession = null;
		try {
			listSession = getListSession(key, request);
		}
		catch (ListSessionExpiredException e) {
			log.debug("Session expired - creating a new one ...");
		}
		if (listSession == null) {
			ListDefinition listDef = editorRepository.getListDefinition(editorId);
			Assert.notNull(listDef, "No such ListDefinition: " + editorId);

			MessageResolver messageResolver = new MessageResolver(messageSource,
					messageCodesResolver, RequestContextUtils.getLocale(request));

			listSession = new ListSession(key, listDef, parentId, parentEditorId,
					messageResolver, request.getContextPath(), 
					editorRepository.getFormRepository(),
					formContextFactory, transactionManager);

			if (choose != null) {
				listSession.setChooserTarget(editorRepository.getEditorDefinition(choose));
			}

			HttpSession httpSession = request.getSession();
			httpSession.setAttribute(key, listSession);
			sessions.add(listSession);

			SessionReferenceRemover.removeFromCollectionOnInvalidation(
					httpSession, sessions, listSession);

		}
		return listSession;
	}

	protected ListSession getListSession(String key, HttpServletRequest request)
			throws ListSessionExpiredException {

		ListSession session = ListSession.getListSession(request, key);
		if (session != null) {
			//Trigger a modification check:
			editorRepository.getListRepository().getListConfig(session.getListId());
			if (!session.isExpired()) {
				return session;
			}
		}
		throw new ListSessionExpiredException();

	}

	public void beanReconfigured(ConfigurableBean bean) {
		Iterator<ListSession> it = sessions.iterator();
		while (it.hasNext()) {
			ListSession session = it.next();
			log.info("Invalidating session " + session.getKey());
			session.invalidate();
			it.remove();
		}
	}

	public ListModel getModel(String key, String expandedId, HttpServletRequest request)
			throws ListSessionExpiredException {

		return getListSession(key, request).getModel(expandedId, request);
	}

	public String getFilterFormHtml(String key,	HttpServletRequest request)
			throws ListSessionExpiredException {

		return getListSession(key, request).getFilterFormHtml();
	}

	public List<CommandButton> getListCommands(String key,
			HttpServletRequest request) throws ListSessionExpiredException {
		
		return getListSession(key, request).getListCommandButtons(request);
	}
	
	public List<CommandButton> getFormCommands(String key, String objectId,
			HttpServletRequest request) throws ListSessionExpiredException {

		return getListSession(key, request).getFormCommandButtons(objectId, request);
	}
	
	public CommandResult execCommand(String key, List<ListItem> items,
			String commandId, boolean confirmed,
			HttpServletRequest request, HttpServletResponse response)
			throws ListSessionExpiredException {

		if (items.size() > 1) {
			return getListSession(key, request).execBatchCommand(
					items, commandId, confirmed, request, response);
		}
		ListItem item = null;
		if (items.size() == 1) {
			item = items.get(0);
		}
		return getListSession(key, request).execCommand(
				item, commandId, confirmed, request, response);
	}

	public ListModel filter(String key, Map<String, String> filter, HttpServletRequest request)
			throws ListSessionExpiredException {
		return getListSession(key, request).filter(filter, request);
	}

	public ListModel gotoPage(String key, int page, HttpServletRequest request)
			throws ListSessionExpiredException {

		return getListSession(key, request).gotoPage(page, request);
	}

	public ListModel sort(String key, String property,
			HttpServletRequest request) throws ListSessionExpiredException {

		return getListSession(key, request).sort(property, request);
	}
	
	public ListModel getChildren(String key, String parentId,
			HttpServletRequest request) throws ListSessionExpiredException {
		
		return getListSession(key, request).getChildren(parentId, request);
	}
}
