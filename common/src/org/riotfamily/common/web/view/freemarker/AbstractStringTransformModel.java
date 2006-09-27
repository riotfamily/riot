package org.riotfamily.common.web.view.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;

public abstract class AbstractStringTransformModel 
		implements TemplateTransformModel {

	protected abstract String transform(String s, Map args)
			throws IOException;
	
	public Writer getWriter(final Writer out, Map args)
			throws TemplateModelException, IOException {

		final Map unwrappedArgs = new HashMap();
		if (args != null) {
			BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
			Iterator it = args.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object arg = wrapper.unwrap((TemplateModel) entry.getValue());
				unwrappedArgs.put(entry.getKey(), arg);
			}
		}

		final StringBuffer buf = new StringBuffer();
		return new Writer() {
			public void write(char cbuf[], int off, int len) {
				buf.append(cbuf, off, len);
			}

			public void flush() throws IOException {
				out.flush();
			}

			public void close() throws IOException {
				out.write(transform(buf.toString(), unwrappedArgs));
			}
		};
	}

}
