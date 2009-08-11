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
