package org.riotfamily.website.hyphenate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.davidashen.text.Hyphenator;
import net.davidashen.util.ErrorHandler;

public class CommonsLoggingErrorHandler implements ErrorHandler {

	private String fileName;
	
	private Log log;
	
	public CommonsLoggingErrorHandler(String fileName) {
		this.fileName = fileName + ": "; 
		this.log = LogFactory.getLog(Hyphenator.class);
	}
	
	public CommonsLoggingErrorHandler(Log log) {
		this.log = log;
	}
	
	public boolean isDebugged(String guard) {
		return false;
	}
	
	public void debug(String guard, String s) {
	}

	public void error(String s) {
		log.warn(fileName + s);
	}

	public void exception(String s, Exception e) {
		log.warn(fileName + s);
	}

	public void info(String s) {
		log.debug(fileName + s);
	}

	public void warning(String s) {
		log.debug(fileName + s);
	}

}
