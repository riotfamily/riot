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
package org.riotfamily.riot.list.command.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.CommandContext;
import org.springframework.beans.factory.BeanNameAware;

/**
 * Abstract baseclass for commands.
 */
public abstract class AbstractCommand implements Command, BeanNameAware {

	private static final String COMMAND_NAME_SUFFIX = "Command";

	private final String COMMAND_MESSAGE_PREFIX = "command.";
	
	protected Log log = LogFactory.getLog(getClass());
	
	private String id;
	
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
	 * Always returns <code>null</code>. Sublasses may override this method
	 * in order to display a confirmation message before the command is
	 * executed.
	 */
	public String getConfirmationMessage(CommandContext context) {
		return null;
	}

	/**
	 * Returns a label by resolving the message-key 
	 * <code>command.<i>labelKeySuffix</i></code>, where <i>labelKeySuffix</i>
	 * is the String returned by {@link #getLabelKeySuffix(CommandContext)}. 
	 */
	public String getLabel(CommandContext context) {
		String key = getLabelKeySuffix(context);
		return context.getMessageResolver().getMessage(
				COMMAND_MESSAGE_PREFIX + key, null, 
				FormatUtils.camelToTitleCase(key));
	}
	
	/**
	 * Returns the command's id. Subclasses may override this method if the
	 * label depends on the context.
	 * 
	 * @see #getLabel(CommandContext)
	 */
	protected String getLabelKeySuffix(CommandContext context) {
		return getId();
	}
	
	/**
	 * Returns the command's id. Subclasses may override this method if the
	 * action depends on the context.
	 */
	public String getAction(CommandContext context) {
		return getId();
	}

	/**
	 * Always returns <code>true</code>. Subclasses may override this method
	 * to disable the command depending on the context.
	 */
	public boolean isEnabled(CommandContext context) {
		return true;
	}

}
