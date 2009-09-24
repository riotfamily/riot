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
package org.riotfamily.common.css;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.riotfamily.common.util.Generics;

/**
 * Reads config files with multiple sections. Example:
 * <pre>
 * var1 = foo
 * 
 * [section1]
 * ; some comment
 * var1 = 123
 * var2 = false
 * 
 * [section2]
 * var1 = something ; another comment
 * var2 = true
 * </pre>
 * <p>
 * Values that are defined before the first section is declared, will 
 * automatically be placed in a section called 'global'.
 * </p> 
 * <p>
 * The strings 'true' and 'false' are converted to java.lang.Boolean, whereas
 * strings that can be parsed as integer value are converted to java.lang.Integer.
 * All other values remain unmodified. 
 * </p>
 */
public class IniFile {

	public static final String GLOBAL_SECTION = "global";
	
	private File file;
	
	HashMap<String, Map<String,Object>> sections = Generics.newHashMap();
	
	Map<String, Object> section;
	
	private long lastModified;
	
	public IniFile(File file) throws IOException {
		this.file = file;
		load();
	}

	public long lastModified() {
		return file.lastModified();
	}
	
	public synchronized Map<String, Map<String,Object>> getSections() {
		if (file.lastModified() > lastModified) {
			try {
				load();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return sections;
	}
	
	private synchronized void load() throws IOException {
		lastModified = file.lastModified();
		sections.clear();
		setSection(GLOBAL_SECTION);
		BufferedReader br = new BufferedReader(new FileReader(file));
		for (String ln = br.readLine(); ln != null; ln = br.readLine()) {
			ln = ln.trim();
			if (ln.length() > 0 && ln.charAt(0) != ';') {
				if (ln.charAt(0) != '[' || ln.indexOf(']') == -1) {
					int index = ln.indexOf(';');
					if (index != -1) {
						ln = ln.substring(0, index).trim();
					}
					index = ln.indexOf('=');
					if (index != -1) {
						String key = ln.substring(0, index).trim();
						String value = ln.substring(index + 1).trim();
						section.put(key, convertString(value));
					}
				}
            	else {
            		setSection(ln.substring(1, ln.indexOf(']')).trim());
            	}
			}
		}
	}
	
	private void setSection(String name) {
		section = sections.get(name);
		if (section == null) {
			section = Generics.newHashMap();
			sections.put(name, section);
		}
	}
	
	private Object convertString(String s) {
		if (s.equals("true")) {
			return Boolean.TRUE;
		}
		if (s.equals("false")) {
			return Boolean.FALSE;
		}
		try {
			return Integer.valueOf(s);
		}
		catch (NumberFormatException e) {
		}
		return s;
	}

}
