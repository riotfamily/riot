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

import java.util.LinkedList;
import java.util.List;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.editor.ui.EditorReference;

/**
 *
 */
public class GroupDefinition extends AbstractEditorDefinition {

	protected static final String TYPE_GROUP = "group";
	
	private List editorDefinitions;
	
	public GroupDefinition(EditorRepository editorRepository) {
		super(editorRepository, TYPE_GROUP);
	}
	
	public List getEditorDefinitions() {
		return editorDefinitions;
	}

	public void setEditorDefinitions(List editorDefinitions) {
		this.editorDefinitions = editorDefinitions;
	}

	public void addEditorDefinition(EditorDefinition editorDefinition) {
		if (editorDefinitions == null) {
			editorDefinitions = new LinkedList();
		}
		editorDefinitions.add(editorDefinition);
		editorDefinition.setParentEditorDefinition(this);
	}

	public EditorReference createEditorPath(String objectId, String parentId,
			MessageResolver messageResolver) {

		EditorReference parent = null;
		if (getParentEditorDefinition() != null) {
			parent = getParentEditorDefinition().createEditorPath(
					parentId, null, messageResolver);
		}
		EditorReference component = createReference(messageResolver);
		
		component.setParent(parent);
		return component;
	}

	public EditorReference createEditorPath(Object bean,
			MessageResolver messageResolver) {

		EditorReference component = null;
		EditorReference parent = null;
		if (getParentEditorDefinition() != null) {
			parent = getParentEditorDefinition().createEditorPath(
					bean, messageResolver);
			
			component = createReference(messageResolver);
			component.setParent(parent);
		}
		else {
			component = createReference(messageResolver);
		}
		return component;
	}

	public EditorReference createReference(String parentId, 
			MessageResolver messageResolver) {
		
		return createReference(messageResolver);
	}
	
	private EditorReference createReference(MessageResolver messageResolver) {
		
		EditorReference ref = new EditorReference();
		ref.setEditorType(TYPE_GROUP);
		ref.setIcon(getIcon());
		
		String defaultLabel = FormatUtils.camelToTitleCase(getId());
		ref.setLabel(messageResolver.getMessage(getMessageKey().toString(), 
				null, defaultLabel));
		
		ref.setDescription(messageResolver.getMessage(
				getMessageKey().append(".description").toString(), null, null));
		
		ref.setEditorUrl(getEditorUrl(null, null));
		return ref;
	}

}
