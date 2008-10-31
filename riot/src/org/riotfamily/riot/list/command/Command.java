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
package org.riotfamily.riot.list.command;

import org.riotfamily.common.i18n.MessageResolver;



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
	 * Sets a unique identifier used to reference the command.
	 */
	public void setId(String id);
	
	
	public String getAction();
	
	/**
	 * Executes the command.
	 */
	public CommandResult execute(CommandContext context);

	/**
	 * Returns a localized message that is displayed to the user asking for
	 * a confirmation. Implementors may return <code>null</code> if no 
	 * confirmation is needed. Otherwise a dialog containing an <i>Ok</i> and a
	 * <i>Cancel</i> button is displayed. If the user clicks <i>Ok</i> 
	 * the command is executed otherwise no action takes place.
	 */
	public String getConfirmationMessage(CommandContext context);
	
	/**
	 * Implementors may inspect the given context to decide whether the
	 * command should be enabled. Commands don't need to check the
	 * {@link org.riotfamily.riot.security.policy.AuthorizationPolicy policy} since
	 * commands will be automatically disabled if the action returned by
	 * {@link #getAction()} is denied.
	 */
	public boolean isEnabled(CommandContext context);
	
	/**
	 * Returns whether the command should be shown beside the form.
	 */
	public boolean isShowOnForm();
		
	public String getLabel(MessageResolver messageResolver);
	
	/**
	 * Returns the CSS class that is assigned to command's HTML element and
	 * therefore defines which icon is displayed. If no class is set, the 
	 * default implementation will return the action instead.
	 */
	public String getStyleClass();
	
	/**
	 * Returns a CSS class that is added to the list of class names of the
	 * whole item/row. The default implementation always returns
	 * <code>null</code>. Subclasses may override this method to highlight
	 * a list item depending on the context.
	 */
	public String getItemStyleClass(CommandContext context);
	
}
