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
	
	public static DisplayDefinition getParentDisplayDefinition(EditorDefinition def) {
		if (def == null) {
			return null;
		}
		EditorDefinition parentDef = def.getParentEditorDefinition();
		while (parentDef != null) {
			if (parentDef instanceof DisplayDefinition) {
				return (DisplayDefinition) parentDef;
			}
			parentDef = parentDef.getParentEditorDefinition();
		}
		return null;
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
