package org.riotfamily.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

public final class SpringUtils {

	private SpringUtils() {
	}
	
	public static<T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} 
		catch (InstantiationException e) {
			throw new BeanCreationException("Instantiation failed", e);
		}
		catch (IllegalAccessException e) {
			throw new BeanCreationException("Instantiation failed", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static<T> Map<String, T> beansOfTypeIncludingAncestors(
			ListableBeanFactory lbf, Class<T> type) {
		
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(lbf, type);
	}
	
	@SuppressWarnings("unchecked")
	public static<T extends Ordered> List<T> 
			orderedBeansIncludingAncestors(
			ListableBeanFactory lbf, Class<T> type) {
		
		Map<String, T> map = beansOfTypeIncludingAncestors(lbf, type);
		ArrayList<T> beans = new ArrayList<T>(map.values());
		Assert.notEmpty(beans, "At last one bean of type '" + type.getName()
				+ "' must be present.");
		
		Collections.sort(beans, new OrderComparator());
		return beans;
	}

}
