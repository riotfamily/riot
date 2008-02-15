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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.command.core;

import java.util.ArrayList;
import java.util.List;

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.riot.list.command.BatchCommand;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ReloadResult;
import org.springframework.util.ObjectUtils;

/**
 * Generic command that can be used to toggle a boolean flag.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PublishCommand extends AbstractCommand implements BatchCommand {

	public static final String ACTION_PUBLISH = "publish";
	
	public static final String ACTION_UNPUBLISH = "unpublish";
	
	String publishedProperty = "published";
	
	Object publishedValue = Boolean.TRUE;
	
	Object unpublishedValue = Boolean.FALSE;
	
	
	public void setPublishedProperty(String publishedProperty) {
		this.publishedProperty = publishedProperty;
	}

	public void setPublishedValue(Object publishedValue) {
		this.publishedValue = publishedValue;
	}

	public void setUnpublishedValue(Object unpublishedValue) {
		this.unpublishedValue = unpublishedValue;
	}

	protected String getAction(CommandContext context) {
		return isPublished(context.getBean()) 
				? ACTION_UNPUBLISH : ACTION_PUBLISH;
	}
	
	protected String getMessageKey(String action, boolean batch) {
		StringBuffer sb = new StringBuffer();
		sb.append("confirm.").append(getId()).append('.').append(action);
		if (batch) {
			sb.append(".selected");
		}
		return sb.toString();
	}
	
	public String getConfirmationMessage(CommandContext context) {
		String action = getAction(context);
		String key = getMessageKey(action, false);
		Object[] args = getDefaultMessageArgs(context);
		return context.getMessageResolver().getMessage(key,	args, key);
	}
	
	public String getBatchConfirmationMessage(CommandContext context, String action) {
		String key = getMessageKey(action, true);
		return context.getMessageResolver().getMessage(key,	key);
	}
	
	public List getBatchStates(CommandContext context) {
		List states = new ArrayList();
		states.add(getState(context, ACTION_PUBLISH));
		states.add(getState(context, ACTION_UNPUBLISH));
		return states;
	}
	
	public CommandResult execute(CommandContext context) {
		Object bean = context.getBean();
		Object value = isPublished(bean) ? unpublishedValue : publishedValue;
		PropertyUtils.setProperty(bean, publishedProperty, value);
		context.getDao().update(bean);
		return new ReloadResult();
	}

	protected boolean isPublished(Object bean) {
		return ObjectUtils.nullSafeEquals(publishedValue, 
				PropertyUtils.getProperty(bean, publishedProperty));
	}
	
}
