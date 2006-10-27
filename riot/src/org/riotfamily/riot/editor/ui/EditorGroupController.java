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
package org.riotfamily.riot.editor.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.GroupDefinition;
import org.riotfamily.riot.security.AccessController;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 */
public class EditorGroupController implements EditorController, 
		MessageSourceAware, UrlMappingAware, BeanNameAware {

	private EditorRepository editorRepository;
	
	private MessageSource messageSource;
	
	private String editorIdAttribute = "editorId";
	
	private String modelKey = "group";
	
	private UrlMapping urlMapping;
	
	private String beanName;
	
	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	private String viewName = ResourceUtils.getPath(
			EditorGroupController.class, "EditorGroupView.ftl");
	
	public EditorGroupController(EditorRepository editorRepository) {
		this.editorRepository = editorRepository;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		String editorId = (String) request.getAttribute(editorIdAttribute);
		
		GroupDefinition groupDefinition = 
				editorRepository.getGroupDefinition(editorId);
		
		Assert.notNull(groupDefinition, "No such group: " + editorId);
		
		if (!AccessController.isGranted(ACTION_VIEW, null, groupDefinition)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
		
		MessageResolver messageResolver = new MessageResolver(messageSource, 
				editorRepository.getMessageCodesResolver(), 
				RequestContextUtils.getLocale(request));
				
		EditorGroup group = new EditorGroup();
		group.setId(groupDefinition.getId());
		group.setTitle(groupDefinition.createReference(null, messageResolver).getLabel());
		
		Iterator ed = groupDefinition.getEditorDefinitions().iterator();
		while (ed.hasNext()) {
			EditorDefinition editor = (EditorDefinition) ed.next();
			if (!editor.isHidden() && AccessController.isGranted(ACTION_VIEW, null, editor)) {
				group.addReference(editor.createReference(null, messageResolver));
			}
		}

		return new ModelAndView(viewName, modelKey, group);
	}

	public Class getDefinitionClass() {
		return GroupDefinition.class;
	}

	public String getUrl(String editorId, String objectId, String parentId) {
		Map attrs = Collections.singletonMap(editorIdAttribute, editorId);
		return urlMapping.getUrl(beanName, attrs);
	}
	
}
