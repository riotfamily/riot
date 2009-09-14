/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.view;

import java.io.IOException;
import java.io.Writer;

import org.riotfamily.cachius.CacheContext;
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
		CacheContext.error();
	}
}
