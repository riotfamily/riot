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

import org.riotfamily.riot.dao.ParentChildDao;
import org.riotfamily.riot.dao.RiotDao;
import org.springframework.util.Assert;

/**
 *
 */
public final class EditorDefinitionUtils {

	private EditorDefinitionUtils() {
	}
	
	public static ListDefinition getListDefinition(
			EditorRepository repository, String editorId) {
		
		EditorDefinition def = repository.getEditorDefinition(editorId);
		return getListDefinition(def);
	}
	
	public static ListDefinition getListDefinition(EditorDefinition def) {
		if (def instanceof ListDefinition) {
			return (ListDefinition) def;
		}
		else {
			return getParentListDefinition(def);
		}
	}
		
	public static ListDefinition getParentListDefinition(EditorDefinition def) {
		if (def == null) {
			return null;
		}
		EditorDefinition parentDef = def.getParentEditorDefinition();
		while (parentDef != null) {
			if (parentDef instanceof ListDefinition) {
				return (ListDefinition) parentDef;
			}
			parentDef = parentDef.getParentEditorDefinition();
		}
		return null;
	}
	
	public static ListDefinition getNextListDefinition(
			ListDefinition start, ListDefinition destination) {
		
		ListDefinition def = destination;
		ListDefinition parent = getParentListDefinition(def); 
		while (parent != start && parent != null) {
			def = parent;
			parent = getParentListDefinition(def);
		}
		return def;
	}

	public static String getObjectId(EditorDefinition def, Object item) {
		ListDefinition listDef = getListDefinition(def);
		return listDef.getListConfig().getDao().getObjectId(item);
	}
	
	public static Object loadBean(EditorDefinition def, String objectId) {
		ListDefinition listDef = getListDefinition(def);
		return listDef.getListConfig().getDao().load(objectId);
	}

	public static Object getParent(EditorDefinition def, Object bean) {
		ListDefinition listDef = getListDefinition(def);
		RiotDao dao = listDef.getListConfig().getDao();
		if (dao instanceof ParentChildDao) {
			ParentChildDao parentChildDao = (ParentChildDao) dao;
			return parentChildDao.getParent(bean);
		}
		return null;
	}
	
	public static String getParentId(EditorDefinition def, Object bean) {
		Object parent = getParent(def, bean);
		if (parent != null) {
			return getObjectId(def.getParentEditorDefinition(), parent);
		}
		return null;
	}
	
	public static Object loadParent(EditorDefinition def, String parentId) {
		if (parentId != null) {
			ListDefinition listDef = getParentListDefinition(def);
			Assert.notNull(listDef, "No parent ListDefinition found for editor "
					+ def.getId());
			
			return loadBean(listDef, parentId);
		}
		return null;
	}
}
