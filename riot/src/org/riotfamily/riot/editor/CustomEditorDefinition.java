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
import org.riotfamily.riot.editor.ui.EditorReference;


public class CustomEditorDefinition extends AbstractDisplayDefinition {

	protected static final String TYPE_CUSTOM = "custom";

	private CustomEditor editor;

	private String target;

	public CustomEditorDefinition(EditorRepository editorRepository) {
		super(editorRepository, TYPE_CUSTOM);
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setEditor(CustomEditor editor) {
		this.editor = editor;
	}

	public EditorReference createReference(String objectId,
			MessageResolver messageResolver) {

		EditorReference ref = super.createReference(objectId, messageResolver);
		ref.setTargetWindow(target);
		return ref;
	}

	public String getTargetUrl(String objectId, String parentId) {
		Object bean = null;
		if (objectId != null) {
			bean = loadBean(objectId);
		}
		return editor.getUrl(bean, parentId);
	}

	public String getEditorUrl(String objectId, String parentId) {
		if (target != null) {
			return getTargetUrl(objectId, parentId);
		}
		//FIXME Get /riot prefix from RiotRuntime
		StringBuffer sb = new StringBuffer();
		sb.append("/riot/custom/").append(getId());
		if (objectId != null) {
			sb.append("?objectId=").append(objectId);
		}
		else if (parentId != null) {
			sb.append("?parentId=").append(parentId);
		}
		return sb.toString();
	}

}
