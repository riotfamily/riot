package org.riotfamily.core.screen.list.command.impl;


/**
 * Command that "descends" to a nested screen. Technically it does the same as 
 * the {@link EditCommand} but uses a different action (<code>"go"</code>)
 * and icon (<code>"bullet_go"</code>).
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class GoCommand extends EditCommand {

	@Override
	protected String getIcon(String action) {
		return "bullet_go";
	}

}
