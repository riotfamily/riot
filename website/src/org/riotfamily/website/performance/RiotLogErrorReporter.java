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
package org.riotfamily.website.performance;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.riotfamily.common.util.RiotLog;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class RiotLogErrorReporter implements ErrorReporter {

	private RiotLog log;
	
	private String defaultSourceName;
	
	public RiotLogErrorReporter(String categoryName) {
		this(RiotLog.get(categoryName));
	}
	
	public RiotLogErrorReporter(RiotLog log) {
		this.log = log;
	}

	public void setSourceName(String sourceName) {
		this.defaultSourceName = sourceName;
	}
	
	public void warning(String message, String sourceName,
            int line, String lineSource, int lineOffset) {
		
       	log.warn(formatMessage(message, sourceName, line, lineSource, lineOffset));
    }

    public void error(String message, String sourceName,
            int line, String lineSource, int lineOffset) {
    	
       	log.error(formatMessage(message, sourceName, line, lineSource, lineOffset));
    }
    
    protected String formatMessage(String message, String sourceName, int line, String lineSource, int lineOffset) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(message);
    	if (sourceName == null) {
    		sourceName = defaultSourceName;
    	}
    	if (sourceName != null) {
    		sb.append(" in ").append(sourceName);
    	}
    	if (lineOffset > 0) {
    		sb.append(" (line ")
    				.append(line)
    				.append(", column ")
    				.append(lineOffset)
    				.append(")");
    	}
    	return sb.toString();	
    }

    public EvaluatorException runtimeError(String message, String sourceName,
            int line, String lineSource, int lineOffset) {
        
    	error(message, sourceName, line, lineSource, lineOffset);
        return new EvaluatorException(message);
    }
}
