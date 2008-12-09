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
package org.riotfamily.riot.editor;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.riot.editor.ui.CustomEditorController;
import org.riotfamily.riot.editor.ui.EditorReference;


public class CustomEditorDefinition extends AbstractObjectEditorDefinition {

	protected static final String TYPE_CUSTOM = "custom";

	private String handler;
	
	private String url;

	private String target;

	
	public String getEditorType() {
		return TYPE_CUSTOM;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setHandler(String handler) {
		this.handler = handler;
	}

	public EditorReference createReference(String objectId,
			MessageResolver messageResolver) {

		EditorReference ref = super.createReference(objectId, messageResolver);
		ref.setTargetWindow(target);
		ref.setDescription(messageResolver.getMessage(
				getMessageKey().append(".description").toString(), null, null));
		
		return ref;
	}

	public String getTargetUrl(String objectId, String parentId, 
			String parentEditorId) {
		
		if (handler != null) {
			return getEditorRepository().getRiotRuntime().getUrlForHandler(
					handler, objectId);
		}
		if (url != null) {
			if (objectId != null) {
				return ServletUtils.addParameter(url, "objectId", objectId);
			}
			return url;
		}
		return null;
	}

	public String getEditorUrl(String objectId, String parentId, 
			String parentEditorId) {
		
		if (target != null) {
			return getTargetUrl(objectId, parentId, parentEditorId);
		}
		return super.getEditorUrl(objectId, parentId, parentEditorId);
	}
	
	protected String getEditorUrlWithinServlet(String objectId, String parentId,
			String parentEditorId) {
		
		return CustomEditorController.getUrl(getId(), objectId, parentId, 
				parentEditorId);
	}

}
