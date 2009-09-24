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
package org.riotfamily.core.resource;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.CodeRevealingMessageSource;
import org.riotfamily.common.web.cache.CacheKeyAugmentor;

/**
 * CacheKeyAugmentor that adds a String to the cacheKey if message code 
 * revelation is turned on.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class CodeRevelationCacheKeyAugmentor implements CacheKeyAugmentor {

	private CodeRevealingMessageSource messageSource;
	
	public CodeRevelationCacheKeyAugmentor(CodeRevealingMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void augmentCacheKey(StringBuilder key, HttpServletRequest request) {
		if (messageSource.isRevealCodes()) {
			key.append(";revealedMessageCodes");
		}
	}

}
