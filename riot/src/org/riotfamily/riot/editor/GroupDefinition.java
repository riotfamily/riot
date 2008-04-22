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
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.editor.ui.EditorReference;

public class GroupDefinition extends AbstractObjectEditorDefinition {

	protected static final String TYPE_GROUP = "group";
	
	public GroupDefinition(EditorRepository editorRepository) {
		setEditorRepository(editorRepository);
	}

	public String getLabel(String objectId, MessageResolver messageResolver) {
		Object bean = null;
		if (objectId != null) {
			bean = loadBean(objectId);
		}
		return getLabel(bean, messageResolver);
	}
	
	public String getLabel(Object object, MessageResolver messageResolver) {
		if (object == null) {
			if (getName() == null) {
				return null;
			}
			String defaultLabel = FormatUtils.xmlToTitleCase(getName());
			return messageResolver.getMessage(getMessageKey().toString(),
					null, defaultLabel);
		}
		return super.getLabel(object, messageResolver);
	}
	
	public EditorReference createReference(String objectId,
			MessageResolver messageResolver) {
		
		EditorReference ref = super.createReference(objectId, messageResolver);
		ref.setDescription(messageResolver.getMessage(
				getMessageKey().append(".description").toString(), null, null));
		
		return ref;
	}
	
	protected String getEditorUrlWithinServlet(String objectId,
			String parentId, String parentEditorId) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("/group/").append(getId());
		if (objectId != null) {
			sb.append("?objectId=").append(objectId);
		}
		return sb.toString();
	}

	public String getEditorType() {
		return TYPE_GROUP;
	}

}
