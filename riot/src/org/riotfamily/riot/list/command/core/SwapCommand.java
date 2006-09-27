package org.riotfamily.riot.list.command.core;

import org.riotfamily.riot.dao.SwappableItemDao;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ReloadResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.ui.render.RenderContext;

/**
 * Command that swaps two items in a list.
 */
public class SwapCommand extends AbstractCommand {
	    
    private int swapWith;
    
	public void setSwapWith(int swapWith) {
		this.swapWith = swapWith;
	}
		
	public boolean isEnabled(RenderContext context) {
		if (context.getDao() instanceof SwappableItemDao) {
			int index = context.getParams().getOffset() + context.getRowIndex(); 
			return index + swapWith >= 0 && 
					index + swapWith < context.getItemsTotal();
		}
		return false;
	}

	public CommandResult execute(CommandContext context) {
		SwappableItemDao listModel = (SwappableItemDao) context.getDao();

		ListDefinition listDef = context.getListDefinition();
		String parentId = context.getParentId();
		Object parent = EditorDefinitionUtils.loadParent(listDef, parentId);
		
		listModel.swapEntity(context.getItem(), parent, context.getParams(),
				context.getRowIndex() + swapWith);
		
		return new ReloadResult();
	}
    
}
