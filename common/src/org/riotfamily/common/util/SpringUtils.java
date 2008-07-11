package org.riotfamily.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

public final class SpringUtils {

	private SpringUtils() {
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T classForName(String className) {
		try {
			return (T) Class.forName(className);
		} 
		catch (ClassNotFoundException e) {
			throw new BeanCreationException("Class not found", e);
		}
		catch (ClassCastException e) {
			throw new BeanCreationException("Invalid cast", e);
		}
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
	
	public static<T> T newInstance(String className) {
		Class<T> clazz = classForName(className);
		return newInstance(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T createBean(String className, 
			AutowireCapableBeanFactory beanFactory, int autowire) {
		
		Class<?> beanClass = SpringUtils.classForName(className);
		return (T) beanFactory.createBean(beanClass, autowire, false);
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T getBean(BeanFactory beanFactory, String name, 
			Class<T> requiredType) {
		
		return (T) beanFactory.getBean(name, requiredType);
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T beanOfType(ListableBeanFactory lbf, Class<T> type) {
		return (T) BeanFactoryUtils.beanOfType(lbf, type);
	}
	
	@SuppressWarnings("unchecked")
	public static<T> Map<String, T> beansOfType(
			ListableBeanFactory lbf, Class<T> type) {
		
		return lbf.getBeansOfType(type);
	}
	
	public static<T> Collection<T> listBeansOfType(
			ListableBeanFactory lbf, Class<T> type) {
		
		return beansOfType(lbf, type).values();
	}
	
	@SuppressWarnings("unchecked")
	public static<T> Map<String, T> beansOfTypeIncludingAncestors(
			ListableBeanFactory lbf, Class<T> type) {
		
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(lbf, type);
	}
	
	public static<T> Collection<T> listBeansOfTypeIncludingAncestors(
			ListableBeanFactory lbf, Class<T> type) {
		
		return beansOfTypeIncludingAncestors(lbf, type).values();
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
