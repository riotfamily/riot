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
import java.util.Set;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.util.ObjectUtils;

/**
 * MessageSource that reveals the code(s) used to look-up a message.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class DefaultCodeRevealingMessageSource extends DelegatingMessageSource implements CodeRevealingMessageSource {
	
	private String contextPath = "";
	
	private boolean revealCodes;
	
	private Set<String> doNotReveal;
	
	public void setDoNotReveal(Set<String> doNotReveal) {
		this.doNotReveal = doNotReveal;
	}
	
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	protected String getContextPath() {
		return contextPath;
	}
	
	public boolean isRevealCodes() {
		return revealCodes;
	}

	public void setRevealCodes(boolean revealCodes) {
		this.revealCodes = revealCodes;
	}

	protected boolean shouldBeRevealed(String code) {
		return isRevealCodes() && (doNotReveal == null || !doNotReveal.contains(code));
	}
	
	protected String revealCodes(String message, String... codes) {
		StringBuffer sb = new StringBuffer();
		if (message != null) {
			sb.append(message);
		}
		sb.append("<span class=\"messageCode\" title=\"");
		int i = 0;
		for (String code : codes) {
			sb.append(code);
			if (++i < codes.length) {
				sb.append(" | ");
			}
		}
		sb.append("\"></span>");
		
		return sb.toString();
	}
	
	@Override
	public String getMessage(String code, Object[] args, String defaultMessage,
			Locale locale) {
		
		if (shouldBeRevealed(code)) {
			return revealCodes(super.getMessage(code, args, 
					defaultMessage, locale), code);
		}
		return super.getMessage(code, args, defaultMessage, locale);
	}

	@Override
	public String getMessage(String code, Object[] args, Locale locale) {
		return getMessage(code, args, null, locale);
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
		if (!ObjectUtils.isEmpty(resolvable.getCodes())) {
			if (shouldBeRevealed(resolvable.getCodes()[0])) {
				return revealCodes(super.getMessage(resolvable, locale), resolvable.getCodes());
			}			
		}
		return super.getMessage(resolvable, locale);
	}

}
