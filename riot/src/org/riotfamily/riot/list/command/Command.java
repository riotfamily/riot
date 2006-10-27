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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.command;

import org.riotfamily.riot.list.ui.render.RenderContext;

/**
 * Interface to be implemented by classes that perform operations on lists
 * or list items.
 * 
 * NOTE: Implementations must be thread-safe. 
 */
public interface Command {

	/**
	 * Returns a unique identifier used to reference the command.
	 */
	public String getId();
	
	/**
	 * Executes the command.
	 */
	public CommandResult execute(CommandContext context);

	/**
	 * Returns a localized message that is displayed to the user asking for
	 * a confirmation. Implementors may return <code>null</code> if no 
	 * confirmation is needed. Otherwise a dialog containing an <i>Ok</i> and a
	 * <i>Cancel</i> button will be displayed. If the user clicks <i>Ok</i> 
	 * the command is executed otherwise no action takes place.
	 */
	public String getConfirmationMessage(CommandContext context);
	
	/**
	 * Returns a String identifiying the action that will be performed when
	 * {@link #execute(CommandContext) execute()} is invoked. The String is
	 * used by the {@link org.riotfamily.riot.list.ui.render.CommandRenderer
	 * renderer} to determine the icon to be displayed and passed to the
	 * {@link org.riotfamily.riot.security.policy.AuthorizationPolicy policy} in order
	 * to check whether a user is allowed to execute the command.
	 * 
	 * Typically the method will return the command's id though it's possible
	 * to return different actions depending on the context.
	 */
	public String getAction(CommandContext context);
	
	/**
	 * Implementors may evaluate the given context to decide whether the 
	 * command should be enabled. Commands don't need to check the 
	 * {@link org.riotfamily.riot.security.policy.AuthorizationPolicy policy} since
	 * commands will be automatically disabled if the action returned by
	 * {@link #getAction(CommandContext) getAction()} is denied.
	 * 
	 * In contrast to the other methods a {@link RenderContext RenderContext}
	 * is passed as argument to allow implementors to consider the lists
	 * sort order or the item's index.  
	 */
	public boolean isEnabled(RenderContext context);

}
