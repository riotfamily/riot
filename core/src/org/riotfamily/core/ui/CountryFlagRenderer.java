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
import java.io.StringWriter;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.riotfamily.common.ui.TypedRenderer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CountryFlagRenderer extends TypedRenderer<Locale> {

	private String resourcePath;
	
	private String internationalFlag = "int";
	
	@Autowired
	private ServletContext context;
	
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
				"/org/riotfamily/core/runtime/resources/style/images/flags/"
				+ flag.toLowerCase() + ".gif") != null;
	}
	
	protected void renderFlag(String flag, String title, PrintWriter writer) {
		writer.print("<img class=\"flag\" src=\"");
		writer.print(context.getContextPath());
		writer.print(resourcePath);
		writer.print("style/images/flags/");
		writer.print(flag.toLowerCase());
		writer.print(".gif\"");
		if (title != null) {
			writer.print("title=\"");
			writer.print(title);
			writer.print('"');
		}
		writer.print(" />");
	}
	
	@Override
	protected String render(Locale locale) {
		StringWriter sw = new StringWriter();
		String flag = null;
		String title = null;
		flag = locale.getCountry();
		title = locale.getDisplayName();
		if (!flagExists(flag)) {
			flag = internationalFlag;
		}
		renderFlag(flag, title, new PrintWriter(sw));
		return sw.toString();
	}

}
