package org.riotfamily.website.hyphenate;


import net.davidashen.text.Hyphenator;
import net.davidashen.util.ErrorHandler;

import org.riotfamily.common.util.RiotLog;

public class RiotLogErrorHandler implements ErrorHandler {

	private String fileName;
	
	private RiotLog log;
	
	public RiotLogErrorHandler(String fileName) {
		this.fileName = fileName + ": "; 
		this.log = RiotLog.get(Hyphenator.class);
	}
	
	public RiotLogErrorHandler(RiotLog log) {
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
