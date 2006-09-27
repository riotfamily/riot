package org.riotfamily.riot.list.command.core;

import javax.servlet.http.HttpSession;

import org.riotfamily.riot.dao.CutAndPasteEnabledDao;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ui.EditorReference;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ShowListResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.ui.render.RenderContext;
import org.springframework.util.ObjectUtils;

public class PasteCommand extends AbstractCommand {
	
	public boolean isEnabled(RenderContext context) {
		HttpSession session = context.getRequest().getSession();
		
		String objectId = (String) session.getAttribute(
						CutCommand.OBJECT_ID_ATTRIBUTE);
		
		ListDefinition sourceListDef = (ListDefinition) 
				session.getAttribute(CutCommand.LIST_DEFINITION_ATTRIBUTE);
			
		if (objectId == null || sourceListDef == null) {
			// No object in clipboard
			return false;
		}
		
		RiotDao dao = context.getDao();
		if (!(dao instanceof CutAndPasteEnabledDao)) {
			return false;
		}

		RiotDao sourceDao = sourceListDef.getListConfig().getDao();

		if (!dao.getEntityClass().equals(sourceDao.getEntityClass())) {
			// Entity classes don't match
			return false;
		}	
		
		if (ObjectUtils.nullSafeEquals(context.getParentId(), 
				session.getAttribute(CutCommand.PARENT_ID_ATTRIBUTE))) {
			
			// Parent is the same
			return false;
		}
		
		EditorReference ref = context.getEditorDefinition().createEditorPath(
				null, context.getParentId(), 
				context.getMessageResolver()).getParent();
		
		while (ref != null) {
			if (objectId.equals(ref.getObjectId())) {
				// Cyclic reference
				return false;
			}
			ref = ref.getParent();
		}
		return true;
	}
	
	public CommandResult execute(CommandContext context) {
		HttpSession session = context.getRequest().getSession();

		CutAndPasteEnabledDao listModel = (CutAndPasteEnabledDao) context.getDao();
		
		String objectId = (String) session.getAttribute(
				CutCommand.OBJECT_ID_ATTRIBUTE);
		
		Object item = listModel.load(objectId);
		Object parent = context.getItem();
		
		listModel.addChild(item, parent);

		String previousParentId = (String) session.getAttribute(
				CutCommand.PARENT_ID_ATTRIBUTE);
		
		if (previousParentId != null) {
			ListDefinition sourceListDef = (ListDefinition) 
					session.getAttribute(CutCommand.LIST_DEFINITION_ATTRIBUTE);
			
			Object previousParent = EditorDefinitionUtils.loadParent(
					sourceListDef, previousParentId);
			
			CutAndPasteEnabledDao previousDao = (CutAndPasteEnabledDao)
					sourceListDef.getListConfig().getDao();
			
			previousDao.removeChild(item, previousParent);
		}
				
		session.removeAttribute(CutCommand.OBJECT_ID_ATTRIBUTE);
		session.removeAttribute(CutCommand.PARENT_ID_ATTRIBUTE);
		session.removeAttribute(CutCommand.LIST_DEFINITION_ATTRIBUTE);
		
		return new ShowListResult(context);
	}

}
