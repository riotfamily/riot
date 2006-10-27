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
package org.riotfamily.riot.job.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.riotfamily.riot.job.persistence.JobDao;
import org.riotfamily.riot.job.persistence.JobDetail;
import org.riotfamily.riot.workflow.status.StatusMessage;
import org.riotfamily.riot.workflow.status.StatusMonitor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public class PendingJobMonitor implements StatusMonitor, MessageSourceAware {

	public static final String MESSAGE_KEY = "job.status";
	
	private JobDao dao;
	
	private MessageSource messageSource;
	
	public PendingJobMonitor(JobDao dao) {
		this.dao = dao;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public Collection getMessages(Locale locale) {
		List jobs = dao.getPendingJobDetails();
		if (jobs.isEmpty()) {
			return null;
		}
		ArrayList messages = new ArrayList();
		Iterator it = jobs.iterator();
		while (it.hasNext()) {
			JobDetail detail = (JobDetail) it.next();
			messages.add(new StatusMessage(messageSource.getMessage(MESSAGE_KEY, 
					new Object[] { detail.getName(), new Integer(detail.getState()) }, 
					locale), null));
		}
		return messages;
	}

}
