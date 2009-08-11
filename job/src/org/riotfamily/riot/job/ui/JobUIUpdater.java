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
