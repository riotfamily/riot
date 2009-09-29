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
package org.riotfamily.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import net.davidashen.text.Hyphenator;
import net.davidashen.util.ErrorHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class RiotHyphenator implements InitializingBean {
	
	private Logger log = LoggerFactory.getLogger(RiotHyphenator.class);
	
	private File baseDir;
	
	private Map<String, Hyphenator> hyphenators = Generics.newHashMap();

	public void setBaseDir(Resource resource) throws IOException {
		this.baseDir = resource.getFile();
	}
	
	public void afterPropertiesSet() throws Exception {
		if (baseDir != null && baseDir.exists()) {
			log.debug("Searching for *.tex files in " + baseDir.getAbsolutePath());
			File[] files = baseDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (FormatUtils.getExtension(file.getName()).equals("tex")) {
					String localeCode = FormatUtils.stripExtension(file.getName());
					try {
						Hyphenator hyphenator = new Hyphenator();
						hyphenator.setErrorHandler(new LoggingErrorHandler(file.getName()));
						hyphenator.loadTable(new FileInputStream(file));				
						hyphenators.put(localeCode, hyphenator);
					} 
					catch (ErrorHandler.NotSetException e) {
						log.error("Could not load hyphenation table for locale "
								+ localeCode + e.getMessage());
					}
				}
			}			
		}
	}
	
	public String hyphenate(Locale locale, String text) {
		Hyphenator hyphenator = hyphenators.get(locale.toString());
		if (hyphenator == null) {
			log.debug("couldn't find hyphenator for locale " + locale 
					+ ", now trying to find a hyphenator for language " 
					+ locale.getLanguage());
			
			hyphenator = hyphenators.get(locale.getLanguage());
		}
		
		if (hyphenator != null) {
			String result = hyphenator.hyphenate(text, 2, 2);
			if (log.isDebugEnabled()) {
				log.debug("Hyphenator result: " + result.replaceAll("\u00AD", "-"));
			}
			return result;
		}
		log.warn("No hyphenator found for locale: " + locale);
		return text;
	}
	
	public static class LoggingErrorHandler implements ErrorHandler {

		private String fileName;
		
		private Logger log;
		
		public LoggingErrorHandler(String fileName) {
			this.fileName = fileName + ": "; 
			this.log = LoggerFactory.getLogger(Hyphenator.class);
		}
		
		public LoggingErrorHandler(Logger log) {
			this.log = log;
		}
		
		public boolean isDebugged(String guard) {
			return false;
		}
		
		public void debug(String guard, String s) {
		}

		public void error(String s) {
			log.warn(fileName + s);
		}

		public void exception(String s, Exception e) {
			log.warn(fileName + s);
		}

		public void info(String s) {
			log.debug(fileName + s);
		}

		public void warning(String s) {
			log.debug(fileName + s);
		}

	}


}
