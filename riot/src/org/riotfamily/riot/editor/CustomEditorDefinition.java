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
package org.riotfamily.riot.editor;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.editor.ui.EditorReference;

public class CustomEditorDefinition extends AbstractEditorDefinition {
	
	protected static final String TYPE_CUSTOM = "custom";
		
	private String url;
	
	private String target;
		
	
	public CustomEditorDefinition(EditorRepository editorRepository) {
		super(editorRepository, TYPE_CUSTOM);
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}	

	public String isTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public EditorReference createEditorPath(String objectId, String parentId, 
			MessageResolver messageResolver) {
		
		EditorReference component = createReference(parentId, messageResolver);
		if (getParentEditorDefinition() != null) {
			EditorReference parent = getParentEditorDefinition()
					.createEditorPath(null, null, messageResolver);
			
			component.setParent(parent);
		}
		return component;
	}

	public EditorReference createEditorPath(Object bean, MessageResolver messageResolver) {
		return createEditorPath(null, null, messageResolver);
	}

	public EditorReference createReference(String parentId, 
			MessageResolver messageResolver) {
		
		EditorReference ref = new EditorReference();
		ref.setEditorType(getEditorType());
		ref.setIcon(getIcon());
		
		ref.setLabel(messageResolver.getMessage(
				getMessageKey().toString(), 
				null, FormatUtils.camelToTitleCase(getId())));
		
		ref.setDescription(messageResolver.getMessage(
				getMessageKey().append(".description").toString(), 
				null, null));
		
		ref.setEditorUrl(getEditorUrl(null, null));
		ref.setTargetWindow(target);
		
		return ref;
	}

	public String getEditorUrl(String objectId, String parentId) {
		if (target != null) {
			return url;
		}
		return super.getEditorUrl(objectId, parentId);
	}
	
	

}
