package org.riotfamily.pages.mvc.cache;

import java.lang.reflect.Method;

import org.riotfamily.cachius.Cache;
import org.riotfamily.riot.dao.RiotDao;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.util.Assert;

public class CacheInvalidationAdvice implements AfterReturningAdvice {

	private Cache cache;
	
	private boolean invalidateAll = false;
	
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setInvalidateAll(boolean invalidateAll) {
		this.invalidateAll = invalidateAll;
	}

	public void afterReturning(Object returnValue, Method method, 
			Object[] args, Object target) throws Throwable {
		
		Assert.isInstanceOf(RiotDao.class, target);
		RiotDao dao = (RiotDao) target;
		Object item = args[0];
		String className = dao.getEntityClass().getName();
		if (invalidateAll) {
			cache.invalidateTaggedItems(className);
		}
		String objectId = dao.getObjectId(item);
		cache.invalidateTaggedItems(className + '#' + objectId);
	}

}
