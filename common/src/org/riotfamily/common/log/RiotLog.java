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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Thin wrapper around commons logging that supports varargs. All framework
 * code should use this class so that we can easily switch to another logging
 * framework in future.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class RiotLog {

	private Log log;
	
	private RiotLog(Log log) {
		this.log = log;
	}
	
	public static RiotLog get(Class<?> clazz) {
		return new RiotLog(LogFactory.getLog(clazz));
	}
	
	public static RiotLog get(String name) {
		return new RiotLog(LogFactory.getLog(name));
	}
	
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}
	
	public void trace(Object msg) {
		log.trace(msg);
	}
	
	public void trace(String msg, Object... args) {
		if (log.isTraceEnabled()) {
			log.trace(String.format(msg, args));
		}
	}
	
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}
	
	public void debug(Object msg) {
		log.debug(msg);
	}
	
	public void debug(String msg, Object... args) {
		if (log.isDebugEnabled()) {
			log.debug(String.format(msg, args));
		}
	}
	
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}
	
	public void info(Object msg) {
		log.info(msg);
	}
	
	public void info(String msg, Object... args) {
		if (log.isInfoEnabled()) {
			log.info(String.format(msg, args));
		}
	}
	
	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}
	
	public void warn(Object msg) {
		log.warn(msg);
	}
	
	public void warn(Throwable t) {
		log.warn(t.getMessage(), t);
	}
	
	public void warn(Object msg, Throwable t) {
		log.warn(msg, t);
	}
	
	public void warn(String msg, Throwable t, Object... args) {
		if (log.isWarnEnabled()) {
			log.warn(String.format(msg, args), t);
		}
	}
	
	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}
	
	public void error(Object msg) {
		log.error(msg);
	}
	
	public void error(Throwable t) {
		log.error(t.getMessage(), t);
	}
	
	public void error(Object msg, Throwable t) {
		log.error(msg, t);
	}
	
	public void error(String msg, Throwable t, Object... args) {
		if (log.isErrorEnabled()) {
			log.error(String.format(msg, args), t);
		}
	}
	
	public boolean isFatalEnabled() {
		return log.isFatalEnabled();
	}
	
	public void fatal(Object msg) {
		log.fatal(msg);
	}
	
	public void fatal(Throwable t) {
		log.fatal(t.getMessage(), t);
	}
	
	public void fatal(Object msg, Throwable t) {
		log.fatal(msg, t);
	}
	
	public void fatal(String msg, Throwable t, Object... args) {
		if (log.isFatalEnabled()) {
			log.fatal(String.format(msg, args), t);
		}
	}
}
