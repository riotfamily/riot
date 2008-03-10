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
 *   Alf Werder [alf dot werder at artundweise dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.editor;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.riot.editor.ui.EditorReference;

public class TreeDefinition extends ListDefinition {
	
	protected static final String TYPE_TREE = "tree";
	
	public TreeDefinition(EditorRepository editorRepository) {
		super(editorRepository);
	}
	
	public boolean isNode(Object bean) {
		if (bean == null) {
			return false;
		}
		if (!getBeanClass().isInstance(bean)) {
			return false;
		}
		
		EditorDefinition parentDef = getParentEditorDefinition();
		
		if (parentDef != null && !(parentDef instanceof GroupDefinition)) {
			Class parentClass = parentDef.getBeanClass();
			if (parentClass.isInstance(bean)) {
				return false;
			}
		}
		return true;
	}

	public EditorReference createEditorPath(Object bean,
		MessageResolver messageResolver) {
		
		if (isNode(bean)) {
			EditorReference parent = getDisplayDefinition().createEditorPath(bean, messageResolver);
			String objectId = EditorDefinitionUtils.getObjectId(this, bean);
			
			EditorReference path = createReference(objectId, getId(), messageResolver);
			path.setEditorType("node");
			
			path.setParent(parent);
			
			EditorReference ref = path;
			while (!ref.getEditorType().equals("list")) {
				ref = ref.getParent();
			}
			ref.setEditorUrl(ServletUtils.setParameter(ref.getEditorUrl(), "expand", objectId));
			
			return path;
		}
		return super.createEditorPath(bean, messageResolver);
	}
	
}
