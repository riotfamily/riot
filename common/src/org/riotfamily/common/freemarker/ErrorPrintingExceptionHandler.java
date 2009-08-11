package org.riotfamily.common.freemarker;

import java.io.IOException;
import java.io.Writer;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * TemplateExceptionHandler that prints the error message without stacktrace.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ErrorPrintingExceptionHandler implements TemplateExceptionHandler {

    public void handleTemplateException(TemplateException te, Environment env, 
    		Writer out) throws TemplateException {
        
    	try {
            out.write("[ERROR: " + te.getMessage() + "]");
        }
        catch (IOException e) {
        }
    }

}
