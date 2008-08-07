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
package org.riotfamily.riot.job.ui;

import java.util.Iterator;

import javax.servlet.ServletContext;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ServerContext;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.WebContext;
import org.riotfamily.riot.job.model.JobDetail;
import org.riotfamily.riot.job.model.JobLogEntry;
import org.springframework.web.context.ServletContextAware;

public class JobUIUpdater implements ServletContextAware {

	public static final String JOB_ID_ATTRIBUTE = JobUIUpdater.class.getName() + ".jobId";
			
	private ServletContext servletContext;
		
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void register(WebContext wctx, Long jobId) {		
		wctx.getScriptSession().setAttribute(JOB_ID_ATTRIBUTE, jobId);
	}
	
	public void log(JobLogEntry entry) {
		send(entry.getJobId(), "addLogEntry", entry);
	}
	
	public void updateJob(JobDetail jd) {
		send(jd.getId(), "updateJob", jd);
	}
	
	@SuppressWarnings("unchecked")
	private void send(Long jobId, String functionName, Object arg) {		
		ServerContext serverContext = ServerContextFactory.get(servletContext);
		Iterator<ScriptSession> it = serverContext.getAllScriptSessions().iterator();
		while (it.hasNext()) {
			ScriptSession session = it.next();
			Long pageJobId = (Long) session.getAttribute(JOB_ID_ATTRIBUTE);
			if (jobId.equals(pageJobId)) {
				ScriptBuffer script = new ScriptBuffer();
				script.appendScript(functionName).appendScript("(")
						.appendData(arg).appendScript(");");
				
				session.addScript(script);
			}
		}
	}
	
}
