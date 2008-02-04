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
package org.riotfamily.riot.list.command.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandState;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.riotfamily.riot.runtime.RiotRuntimeAware;
import org.springframework.beans.factory.BeanNameAware;

/**
 * Abstract baseclass for commands.
 */
public abstract class AbstractCommand implements Command, BeanNameAware, 
		RiotRuntimeAware {

	private static final String COMMAND_NAME_SUFFIX = "Command";

	private final String COMMAND_MESSAGE_PREFIX = "command.";

	protected Log log = LogFactory.getLog(getClass());

	private String id;

	private boolean showOnForm;
	
	private String riotServletPrefix;

	public String getId() {
		return id;
	}

	/**
	 * Sets the commandId. If no value is set the bean name will be used
	 * by default.
	 *
	 * @see #setBeanName(String)
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Implementation of the
	 * {@link org.springframework.beans.factory.BeanNameAware BeanNameAware}
	 * interface. If no command id is explicitly set, the bean name will be
	 * used instead. Note that if the name ends with the suffix "Command"
	 * it will be removed from the id.
	 */
	public void setBeanName(String beanName) {
		if (id == null) {
			if (beanName.endsWith(COMMAND_NAME_SUFFIX)) {
				beanName = beanName.substring(0, beanName.length() -
						COMMAND_NAME_SUFFIX.length());
			}
			id = beanName;
		}
	}
	
	/**
	 * Implementation of the {@link RiotRuntimeAware} interface. Allows 
	 * subclasses to call {@link #getRiotServletPrefix()}.
	 */
	public void setRiotRuntime(RiotRuntime runtime) {
		this.riotServletPrefix = runtime.getServletPrefix();
	}
	
	protected String getRiotServletPrefix() {
		return riotServletPrefix;
	}

	/**
	 * Always returns <code>null</code>. Sublasses may override this method
	 * in order to display a confirmation message before the command is
	 * executed.
	 */
	public String getConfirmationMessage(CommandContext context) {
		return null;
	}

	public boolean isShowOnForm() {
		return this.showOnForm;
	}

	public void setShowOnForm(boolean showOnForm) {
		this.showOnForm = showOnForm;
	}

	/**
	 *
	 */
	public CommandState getState(CommandContext context) {
		String action = getAction(context);
		CommandState state = getState(context, action);
		state.setEnabled(isEnabled(context, action));
		return state;
	}
	
	protected CommandState getState(CommandContext context, String action) {
		CommandState state = new CommandState();
		state.setId(getId());
		state.setAction(action);
		state.setLabel(getLabel(context, action));
		state.setStyleClass(getStyleClass(context, action));
		state.setItemStyleClass(getItemStyleClass(context, action));
		return state;
	}

	/**
	 * Returns the command's id. Subclasses may override this method if the
	 * action depends on the context.
	 */
	protected String getAction(CommandContext context) {
		return getId();
	}

	/**
	 * Returns a label by resolving the message-key
	 * <code>command.<i>labelKeySuffix</i></code>, where <i>labelKeySuffix</i>
	 * is the String returned by {@link #getLabelKeySuffix(CommandContext, String)}.
	 */
	protected String getLabel(CommandContext context, String action) {
		String key = getLabelKeySuffix(context, action);
		return context.getMessageResolver().getMessage(
				COMMAND_MESSAGE_PREFIX + key, null,
				FormatUtils.camelToTitleCase(key));
	}

	/**
	 * Returns the command's action. Subclasses may override this method if the
	 * label depends on the context.
	 *
	 * @see #getLabel(CommandContext, String)
	 */
	protected String getLabelKeySuffix(CommandContext context, String action) {
		return action;
	}

	/**
	 * Returns the CSS class that is assigned to command's HTML element and
	 * therefore defines which icon is displayed. The default implementation
	 * returns the given action.
	 */
	protected String getStyleClass(CommandContext context, String action) {
		return action;
	}

	/**
	 * Returns a CSS class that is added to the list of class names of the
	 * whole item/row. The default implementation always returns
	 * <code>null</code>. Subclasses may override this method to highlight
	 * a list item depending on the context.
	 */
	protected String getItemStyleClass(CommandContext context, String action) {
		return null;
	}

	/**
	 * Subclasses may inspect the given context to decide whether the
	 * command should be enabled. Commands don't need to check the
	 * {@link org.riotfamily.riot.security.policy.AuthorizationPolicy policy} since
	 * commands will be automatically disabled if the action returned by
	 * {@link #getAction(CommandContext) getAction()} is denied.
	 * The default implementation always returns <code>true</code>.
	 */
	protected boolean isEnabled(CommandContext context, String action) {
		return true;
	}

}
