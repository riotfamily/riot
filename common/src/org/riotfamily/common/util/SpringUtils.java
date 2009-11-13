package org.riotfamily.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

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
	public static<T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type) {
		return (T) BeanFactoryUtils.beanOfTypeIncludingAncestors(lbf, type);
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
	public static<T> List<T> orderedBeansIncludingAncestors(
			ListableBeanFactory lbf, Class<T> type) {
		
		ArrayList<T> beans = new ArrayList<T>(listBeansOfTypeIncludingAncestors(lbf, type));
		Collections.sort(beans, new AnnotationAwareOrderComparator());
		return beans;
	}
	
	@SuppressWarnings("unchecked")
	public static<T> List<T> orderedBeans(ListableBeanFactory lbf, Class<T> type) {
		ArrayList<T> beans = new ArrayList<T>(listBeansOfType(lbf, type));
		Collections.sort(beans, new AnnotationAwareOrderComparator());
		return beans;
	}
	
	public static WebApplicationContext getWebsiteApplicationContext(
			ServletContext servletContext, String servletName) {
		
		Assert.notNull(servletName, "A servleName must be specified");
		String contextAttribute = DispatcherServlet.SERVLET_CONTEXT_PREFIX 
				+ servletName;
		
		WebApplicationContext ctx = (WebApplicationContext) 
				servletContext.getAttribute(contextAttribute);
		
		Assert.state(ctx != null, "No WebApplicationContext found in the " +
				"ServletContext under the key '" + contextAttribute + "'. " +
				"Make sure your DispatcherServlet is called '" + 
				servletName + "' and publishContext is set to true.");
		
		return ctx;
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T getBean(ServletContext servletContext, 
			String servletName, String beanName) {
		
		return (T) getWebsiteApplicationContext(servletContext, servletName)
				.getBean(beanName);
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T getBean(ServletContext servletContext, 
			String servletName, String beanName, Class<T> requiredType) {
		
		return (T) getWebsiteApplicationContext(servletContext, servletName)
				.getBean(beanName, requiredType);
	}

}
