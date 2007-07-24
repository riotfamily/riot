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
package org.riotfamily.components.macro;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.components.Component;
import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.ComponentVersion;
import org.riotfamily.components.VersionContainer;
import org.riotfamily.components.editor.EditModeUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentMacroHelper {

	private HttpServletRequest request;

	private Collection toolbarScripts;

	private ComponentRepository repository;

	public ComponentMacroHelper(HttpServletRequest request,
			Collection toolbarScripts, ComponentRepository repository) {

		this.request = request;
		this.toolbarScripts = toolbarScripts;
		this.repository = repository;
	}

	public boolean isEditMode() {
		return EditModeUtils.isEditMode(request);
	}

	public Collection getToolbarScripts() {
		return this.toolbarScripts;
	}

	public String getFormUrl(String formId, Long containerId) {
		return repository.getFormUrl(formId, containerId);
	}

	public String tag(VersionContainer container) {
		ComponentVersion version = isEditMode()
				? container.getLatestVersion()
				: container.getLiveVersion();

		TaggingContext.tag(request, version.getType());
		TaggingContext.tag(request, VersionContainer.class.getName()
				+ '#' + container.getId());

		return "";
	}

	public Map getProperties(VersionContainer container) {
		ComponentVersion version = EditModeUtils.isEditMode(request)
				? container.getLatestVersion()
				: container.getLiveVersion();

		Component component = repository.getComponent(version.getType());
		return component.buildModel(version);
	}

}
