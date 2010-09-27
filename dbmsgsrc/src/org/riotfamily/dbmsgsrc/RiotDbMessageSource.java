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
package org.riotfamily.dbmsgsrc;

import java.util.Locale;
import java.util.Set;

import org.riotfamily.common.i18n.DefaultCodeRevealingMessageSource;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlResolver;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.transaction.annotation.Transactional;

public class RiotDbMessageSource extends DefaultCodeRevealingMessageSource {

	private DbMessageSource dbMessageSource;
	
	private Set<String> revealTo = Generics.newHashSet();

	private HandlerUrlResolver handlerUrlResolver;
	
	public RiotDbMessageSource(HandlerUrlResolver handlerUrlResolver) {
		dbMessageSource = new DbMessageSource();
		dbMessageSource.setBundle("riot");
		super.setParentMessageSource(dbMessageSource);
		this.handlerUrlResolver = handlerUrlResolver;
	}
	
	@Override
	public void setParentMessageSource(MessageSource parent) {
		dbMessageSource.setParentMessageSource(parent);
	}
	
	
	@Override
	@Transactional
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
		return super.getMessage(resolvable, locale);
	}
	
	@Override
	@Transactional
	public String getMessage(String code, Object[] args, Locale locale) {
		return super.getMessage(code, args, locale);
	}
	
	@Override
	@Transactional
	public String getMessage(String code, Object[] args, String defaultMessage,
			Locale locale) {

		return super.getMessage(code, args, defaultMessage, locale);
	}
	
	@Override
	public boolean isRevealCodes() {
		RiotUser user = AccessController.getCurrentUser();
		if (user != null) {
			return revealTo.contains(user.getUserId());
		}
		return false;
	}

	@Override
	public void setRevealCodes(boolean revealCodes) {
		RiotUser user = AccessController.getCurrentUser();
		if (user != null) {
			if (revealCodes) {
				revealTo.add(user.getUserId());
			}
			else {
				revealTo.remove(user.getUserId());
			}
		}
	}
	
	@Override
	protected String revealCodes(String message, String... codes) {
		String url = getEditorUrl(codes[0]);
		if (url != null) {
			StringBuilder sb = new StringBuilder();
			if (message != null) {
				sb.append(message);
			}
			sb.append("<span class=\"messageCode\" onclick=\"")
				.append("new riot.window.Dialog({url: '").append(url).append("', ")
				.append("title: '").append(codes[0]).append("', closeButton: true, minHeight: 150});return false\"></span>");
			
			return sb.toString();
		}
		return message;
	}

	private String getEditorUrl(String code) {
		MessageBundleEntry entry = dbMessageSource.getEntry(code, null);
		if (entry != null) {
			return getContextPath() + handlerUrlResolver.getUrlForHandler(
					"editMessageFormController", entry.getId());

		}
		return null;
	}
}
