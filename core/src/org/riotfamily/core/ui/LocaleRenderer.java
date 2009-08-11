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

import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.ui.RenderContext;
import org.springframework.util.StringUtils;

public class LocaleRenderer extends CountryFlagRenderer {

	public LocaleRenderer(String resourcePath) {
		super(resourcePath);
	}

	public void render(Object obj, RenderContext context, PrintWriter writer) {
		if (obj instanceof Locale) {
			TagWriter span = new TagWriter(writer).start("span")
					.attribute("class", "locale").body();
			
			Locale locale = (Locale) obj;
			if (StringUtils.hasLength(locale.getCountry())) {
				renderFlag(locale.getCountry(), null, context, writer);
			}
			else {
				renderFlag(getInternationalFlag(), null, context, writer);	
			}
			writer.print(locale.getDisplayName(context.getMessageResolver().getLocale()));
			span.end();
		}
	}
}
