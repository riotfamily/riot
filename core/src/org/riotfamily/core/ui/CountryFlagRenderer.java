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
package org.riotfamily.core.ui;

import java.io.PrintWriter;
import java.util.Locale;

import org.riotfamily.common.ui.ObjectRenderer;
import org.riotfamily.common.ui.RenderContext;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CountryFlagRenderer implements ObjectRenderer {

	private String resourcePath;
	
	private String internationalFlag = "int";
	
	public CountryFlagRenderer(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public void setInternationalFlag(String internationalFlag) {
		this.internationalFlag = internationalFlag;
	}
	
	protected String getInternationalFlag() {
		return internationalFlag;
	}

	protected boolean flagExists(String flag) {
		return getClass().getResourceAsStream(
				"/org/riotfamily/riot/runtime/resources/style/icons/flags/"
				+ flag.toLowerCase() + ".gif") != null;
	}
	
	protected void renderFlag(String flag, String title, 
			RenderContext context, PrintWriter writer) {
		
		writer.print("<img class=\"flag\" src=\"");
		writer.print(context.getContextPath());
		writer.print(resourcePath);
		writer.print("style/icons/flags/");
		writer.print(flag.toLowerCase());
		writer.print(".gif\"");
		if (title != null) {
			writer.print("title=\"");
			writer.print(title);
			writer.print('"');
		}
		writer.print(" />");
	}
	
	public void render(Object obj, RenderContext context, PrintWriter writer) {
		if (obj != null) {
			String flag = null;
			String title = null;
			if (obj instanceof Locale) {
				Locale locale = (Locale) obj;
				flag = locale.getCountry();
				title = locale.getDisplayName();
			}
			else {
				flag = obj.toString();
			}
			if (!flagExists(flag)) {
				flag = internationalFlag;
			}
			renderFlag(flag, title, context, writer);
		}
	}

}
