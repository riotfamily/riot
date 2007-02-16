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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
