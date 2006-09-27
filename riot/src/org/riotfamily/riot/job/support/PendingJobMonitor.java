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
