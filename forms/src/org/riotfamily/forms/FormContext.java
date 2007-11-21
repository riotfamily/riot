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
package org.riotfamily.forms;

import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.web.file.FileStore;
import org.springframework.beans.PropertyEditorRegistrar;

public interface FormContext {
		
	public Locale getLocale();
	
	public MessageResolver getMessageResolver();
	
	public String getContextPath();
	
	public String getResourcePath();

	public TemplateRenderer getTemplateRenderer();

	public PrintWriter getWriter();

	public void setWriter(PrintWriter writer);
	
	public String getFormUrl();
	
	public String getContentUrl(ContentElement el);
	
	public String getUploadUrl(String uploadId);

	public PropertyEditorRegistrar[] getPropertyEditorRegistrars();

	public List getOptionValuesAdapters();
	
	public FileStore getFileStore(String id);

}
