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
package org.riotfamily.common.io;

import java.io.Reader;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.web.util.JavaScriptUtils;

public class MessageFilterReader extends AbstractTokenFilterReader {

	private MessageSource messageSource;
	
	private String prefix;
	
	private Locale locale;
	
	private boolean escapeJsStrings;
	
	public MessageFilterReader(Reader in, MessageSource messageSource, 
			Locale locale) {
		
		this (in, messageSource, locale, null, false);
	}
	
	public MessageFilterReader(Reader in, MessageSource messageSource, 
			Locale locale, String prefix, boolean escapeJsStrings) {
		
		super(in);
		this.messageSource = messageSource;
		this.locale = locale;
		this.prefix = prefix;
		this.escapeJsStrings = escapeJsStrings;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	protected String getReplacement(String key) {
		if (prefix != null) {
			key = prefix + key;
		}
		String message = messageSource.getMessage(key, null, key, locale);
		if (escapeJsStrings) {
			message = JavaScriptUtils.javaScriptEscape(message);
		}
		return message;
	}
}
