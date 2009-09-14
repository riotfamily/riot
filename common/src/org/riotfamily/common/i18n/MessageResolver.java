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
package org.riotfamily.common.i18n;

import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

public class MessageResolver extends MessageSourceAccessor {
	
	private static final String EMPTY_MESSAGE = new String();
	
	private AdvancedMessageCodesResolver messageCodesResolver;
	
	private MessageSource messageSource;
	
	
	public MessageResolver(MessageSource source, 
			AdvancedMessageCodesResolver resolver, Locale locale) {
		
		super(source, locale);
		this.messageSource = source;
		this.messageCodesResolver = resolver;
	}
	
	public AdvancedMessageCodesResolver getMessageCodesResolver() {
		return this.messageCodesResolver;
	}
	
	public Locale getLocale() {
		return getDefaultLocale();
	}
	
	public MessageSource getMessageSource() {
		return messageSource;
	}
	
	public String getPropertyLabel(String objectName, 
			Class<?> clazz, String property) {
		
		String[] codes = messageCodesResolver.resolveLabelCodes(objectName, clazz, property);
		String defaultMessage = FormatUtils.propertyToTitleCase(property);
		return getMessage(codes, null, defaultMessage);
	}
	
	public String getPropertyLabelWithoutDefault(String objectName, 
			Class<?> clazz, String property) {
		
		String[] codes = messageCodesResolver.resolveLabelCodes(objectName, clazz, property);		
		return getMessage(codes, null, null);
	}
	
	public String getClassLabel(String objectName, Class<?> clazz) {
		String[] codes = messageCodesResolver.resolveLabelCodes(objectName, clazz);
		String defaultMessage = FormatUtils.camelToTitleCase(objectName);
		return getMessage(codes, null, defaultMessage);
	}
		
	public String getPropertyHint(String objectName, 
			Class<?> clazz, String property) {
		
		String[] codes = messageCodesResolver.resolveUICodes(
				objectName, clazz, property, null, "hint");
		
		return getMessage(codes, null, null);
	}
	
	public String getMessage(String[] codes, String defaultMessage) {
		return getMessage(codes, null, defaultMessage);
	}
	
	public String getMessage(String[] codes, Object[] args, 
			String defaultMessage) {
		
		if (defaultMessage == null) {
			defaultMessage = EMPTY_MESSAGE;
		}
		MessageSourceResolvable resolvable = 
				new DefaultMessageSourceResolvable(codes, args, defaultMessage);
		
		String message = messageSource.getMessage(resolvable, getLocale());
		if (message == EMPTY_MESSAGE) {
			return null;
		}
		return message;
	}

}
