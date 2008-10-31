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

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.riotfamily.riot.runtime.RiotRuntimeAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for commands.
 */
public abstract class AbstractCommand implements Command, BeanNameAware, 
		RiotRuntimeAware {

	private static final String COMMAND_NAME_SUFFIX = "Command";

	private final String COMMAND_MESSAGE_PREFIX = "command.";

	protected RiotLog log = RiotLog.get(getClass());

	private String id;
	
	private String beanName;
	
	private String styleClass;

	private boolean showOnForm;
	
	private RiotRuntime runtime;

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		if (id == null) {
			if (beanName != null) {
				id = beanName;
			}
			else {
				id = getClass().getName();
			}
			int i = id.lastIndexOf('.');
			if (i >= 0) {
				id = id.substring(i + 1);
			}
			if (id.endsWith(COMMAND_NAME_SUFFIX)) {
				id = id.substring(0, id.length() - COMMAND_NAME_SUFFIX.length());
			}
			id = StringUtils.uncapitalize(id);
		}
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
		this.beanName = beanName;
	}
	
	/**
	 * Implementation of the {@link RiotRuntimeAware} interface. Allows 
	 * subclasses to call {@link #getRiotServletPrefix()}.
	 */
	public void setRiotRuntime(RiotRuntime runtime) {
		this.runtime = runtime;
	}
	
	protected RiotRuntime getRuntime() {
		return runtime;
	}

	/**
	 * @inheritDoc
	 * 
	 * Always returns <code>null</code>. Sublasses may override this method
	 * in order to display a confirmation message before the command is
	 * executed.
	 */
	public String getConfirmationMessage(CommandContext context) {
		return null;
	}
	
	protected Object[] getDefaultMessageArgs(CommandContext context) {
		Class<?> clazz = context.getListDefinition().getBeanClass();
		Object item = context.getBean();
		String type = context.getMessageResolver().getClassLabel(null, clazz);
		String label = FormatUtils.xmlEscape(context.getListDefinition()
				.getLabel(item, context.getMessageResolver()));
		
		return new Object[] {label, type, context.getObjectId()};
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isShowOnForm() {
		return this.showOnForm;
	}

	public void setShowOnForm(boolean showOnForm) {
		this.showOnForm = showOnForm;
	}

	/**
	 * Returns the command's id.
	 */
	public String getAction() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLabel(MessageResolver messageResolver) {
		return messageResolver.getMessage(
				COMMAND_MESSAGE_PREFIX + getAction(), null,
				FormatUtils.camelToTitleCase(getAction()));
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * If no class is set, the default implementation will return the 
	 * action instead.
	 */
	public String getStyleClass() {
		if (styleClass == null) {
			return getAction();
		}
		return styleClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The default implementation always returns <code>null</code>.
	 */
	public String getItemStyleClass(CommandContext context) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The default implementation always returns <code>true</code>.
	 */
	public boolean isEnabled(CommandContext context) {
		return true;
	}

}
