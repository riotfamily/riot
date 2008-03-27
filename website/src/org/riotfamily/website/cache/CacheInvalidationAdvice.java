/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.cache;

import java.lang.reflect.Method;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.riot.dao.RiotDao;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.util.Assert;

public class CacheInvalidationAdvice implements AfterReturningAdvice {

	private CacheService cacheService;

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {

		Assert.isInstanceOf(RiotDao.class, target);
		RiotDao dao = (RiotDao) target;
		Object item = args[0];
		CacheInvalidationUtils.invalidate(cacheService, dao, item);
	}

}
