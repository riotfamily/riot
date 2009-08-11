package org.riotfamily.core.screen.list.command.impl.support;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;

public abstract class AbstractSingleItemCommand<T> extends AbstractCommand {

	private Class<T> requiredType;
	
	@SuppressWarnings("unchecked")
	private Class<T> getRequiredType() {
		if (requiredType == null) { 
			requiredType = (Class<T>) Generics.getTypeArguments(
					AbstractSingleItemCommand.class, getClass()).get(0);
		}
		return requiredType; 
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public final boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() == 1 && getRequiredType().isInstance(
				selection.getSingleItem().getObject())) {
			
			return isEnabled(context, (T) selection.getSingleItem().getObject());
		}
		return false;
	}
	
	protected boolean isEnabled(CommandContext context, T item) {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public final CommandResult execute(CommandContext context, Selection selection) {
		return execute(context, (T) selection.getSingleItem().getObject());
	}
	
	protected abstract CommandResult execute(CommandContext context, T item);
}
