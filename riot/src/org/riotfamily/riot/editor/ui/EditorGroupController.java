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
package org.riotfamily.riot.editor.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.riot.editor.EditorConstants;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.GroupDefinition;
import org.riotfamily.riot.security.AccessController;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 */
public class EditorGroupController implements Controller, MessageSourceAware {

	private EditorRepository editorRepository;

	private MessageSource messageSource;
	
	private AdvancedMessageCodesResolver messageCodesResolver;

	private String modelKey = "group";


	private String viewName = ResourceUtils.getPath(
			EditorGroupController.class, "EditorGroupView.ftl");

	public EditorGroupController(EditorRepository editorRepository) {
		this.editorRepository = editorRepository;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setMessageCodesResolver(AdvancedMessageCodesResolver messageCodesResolver) {
		this.messageCodesResolver = messageCodesResolver;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String editorId = (String) request.getAttribute(EditorConstants.EDITOR_ID);
		String objectId = request.getParameter("objectId");
		
		GroupDefinition groupDefinition =
				editorRepository.getGroupDefinition(editorId);

		Assert.notNull(groupDefinition, "No such group: " + editorId);

		if (!AccessController.isGranted("use-editor", groupDefinition)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		}

		MessageResolver messageResolver = new MessageResolver(messageSource,
				messageCodesResolver,
				RequestContextUtils.getLocale(request));

		EditorGroup group = new EditorGroup();
		group.setId(groupDefinition.getId());
		group.setTitle(groupDefinition.getLabel(objectId, messageResolver));
		group.setObjectId(objectId);
		
		for (EditorDefinition editor : groupDefinition.getChildEditorDefinitions()) {
			if (!editor.isHidden() && AccessController.isGranted("use-editor", editor)) {
				group.addReference(editor.createReference(objectId, messageResolver));
			}
		}

		return new ModelAndView(viewName, modelKey, group);
	}

}
