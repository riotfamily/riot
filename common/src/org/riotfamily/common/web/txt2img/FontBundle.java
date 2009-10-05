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
package org.riotfamily.common.web.txt2img;

import java.awt.Font;
import java.util.Collection;
import java.util.Locale;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class FontBundle {

	private Logger log = LoggerFactory.getLogger(FontBundle.class);

	private Pattern namePattern = Pattern.compile("^([^_]+?)_(.+?)(\\..+?)$");
	
	private final static String UNNAMED_PREFIX = "unnamed-";
	
	private SortedMap<String,Font> fonts = Generics.newTreeMap();

	public void addFont(Font font) {
		addFont(null, font);
	}
	
	public void addFont(String name, Font font) {
		if (!StringUtils.hasText(name)) {
			name = UNNAMED_PREFIX + fonts.size();
		}
		else {
			Matcher matcher = namePattern.matcher(name);
			if (matcher.find()) {
				name = matcher.group(2);
			}
		}
		log.debug("Adding font [{}] with name '{}'", font.getName(), name);
		Assert.isNull(fonts.put(name, font), "Duplicate name " + name);
	}
	
	public Font getFontFor(String name) {
		return fonts.get(name);
	}
	
	public Font getFontFor(Locale locale) {
		Font result = null;
		if (locale != null) {
			result = getFontFor(locale.toString());
			if (result == null) {
				result = getFontFor(locale.getLanguage());
			}
		}
		return result;
	}
	
	public Collection<Font> getAllFonts() {
		return fonts.values();
	}

}
