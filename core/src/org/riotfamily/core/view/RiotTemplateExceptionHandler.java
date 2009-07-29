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
package org.riotfamily.core.view;

import java.io.IOException;
import java.io.Writer;

import org.riotfamily.cachius.CachiusContext;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.core.security.AccessController;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * FreeMarker TemplateExceptionHandler that only outputs an error message if 
 * the template is requested by an authenticated Riot user: Otherwise the
 * message is silently swallowed. Additionally the handler instructs Cachius 
 * not to cache the output.  
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class RiotTemplateExceptionHandler implements TemplateExceptionHandler {

	public void handleTemplateException(TemplateException te, Environment env, 
			Writer out) throws TemplateException {
		
		if (AccessController.isAuthenticatedUser()) {
			try {
				String message = FormatUtils.xmlEscape(te.getMessage());
	            out.write("[ERROR: " + message + "]");
	        }
	        catch (IOException e) {
	        }
		}
		CachiusContext.preventCaching();
	}
}
