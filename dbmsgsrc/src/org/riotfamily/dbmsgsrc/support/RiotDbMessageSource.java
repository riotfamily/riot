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
package org.riotfamily.dbmsgsrc.support;

import java.util.Set;

import org.riotfamily.common.i18n.CodeRevealingMessageSource;
import org.riotfamily.common.mapping.HandlerUrlResolver;
import org.riotfamily.common.util.Generics;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.springframework.context.MessageSource;
import org.springframework.transaction.PlatformTransactionManager;

public class RiotDbMessageSource extends CodeRevealingMessageSource {

	private DbMessageSource dbMessageSource;
	
	private Set<String> revealTo = Generics.newHashSet();

	private HandlerUrlResolver handlerUrlResolver;
	
	public RiotDbMessageSource(PlatformTransactionManager tx, 
			HandlerUrlResolver handlerUrlResolver) {
		
		dbMessageSource = new DbMessageSource(tx);
		dbMessageSource.setBundle("riot");
		super.setParentMessageSource(dbMessageSource);
		this.handlerUrlResolver = handlerUrlResolver;
	}
	
	@Override
	public void setParentMessageSource(MessageSource parent) {
		dbMessageSource.setParentMessageSource(parent);
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
			sb.append("<span class=\"messageCode\" onclick=\"window.open('")
				.append(url).append("','dbmsgsrc','width=650,height=400');return false\"></span>");
			
			return sb.toString();
		}
		return message;
	}

	private String getEditorUrl(String code) {
		MessageBundleEntry entry = dbMessageSource.getEntry(code, null);
		if (entry != null) {
			return getContextPath() + handlerUrlResolver.getUrlForHandler(null,
					"popupFormController", "riotMessageBundleEntry", entry.getId());

		}
		return null;
	}
}
