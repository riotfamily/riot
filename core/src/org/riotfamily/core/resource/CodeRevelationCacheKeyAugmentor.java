package org.riotfamily.core.resource;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.servlet.CacheKeyAugmentor;
import org.riotfamily.common.i18n.CodeRevealingMessageSource;

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

	public void augmentCacheKey(StringBuffer key, HttpServletRequest request) {
		if (messageSource.isRevealCodes()) {
			key.append(";revealedMessageCodes");
		}
	}

}
