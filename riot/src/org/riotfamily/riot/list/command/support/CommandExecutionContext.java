package org.riotfamily.riot.list.command.support;

import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.list.ui.Constants;
import org.riotfamily.riot.list.ui.ListContext;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * CommandContext used for command execution. 
 */
public class CommandExecutionContext extends AbstractCommandContext {

	private String objectId;

	private int rowIndex;
	
	private Object item;
	
	private boolean confirmed;
	
	public CommandExecutionContext(ListContext context) {
		super(context);
		this.objectId = context.getObjectId();
		this.rowIndex = context.getRowIndex();
		this.confirmed = ServletRequestUtils.getBooleanParameter(
				context.getRequest(), Constants.PARAM_CONFIRMED, false);
	}

	public String getObjectId() {
		return objectId;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public Object getItem() {
		if (item == null) {
			if (objectId != null) {
				item = getDao().load(objectId);
			}
			else if (getParentId() != null) {
				item = EditorDefinitionUtils.loadParent(getListDefinition(), 
						getParentId());
			}
		}
		return item;
	}
	
	public boolean isConfirmed() {
		return this.confirmed;
	}
	
}
