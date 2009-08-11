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
package org.riotfamily.forms;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.riotfamily.common.ui.RenderContext;
import org.springframework.beans.PropertyEditorRegistrar;

public interface FormContext extends RenderContext {
		
	public Locale getLocale();
	
	public String getResourcePath();

	public TemplateRenderer getTemplateRenderer();

	public PrintWriter getWriter();

	public void setWriter(PrintWriter writer);
	
	public String getFormUrl();
	
	public String getContentUrl(ContentElement el);
	
	public String getUploadUrl(String uploadId);

	public Collection<PropertyEditorRegistrar> getPropertyEditorRegistrars();

	public List<OptionsModelAdapter> getOptionsModelAdapters();
	
}
