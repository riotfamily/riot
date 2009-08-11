package org.riotfamily.common.freemarker;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class BeanFactoryTemplateModel implements TemplateHashModel {

	private BeanFactory beanFactory;
	
	private ObjectWrapper objectWrapper;
	
	
	public BeanFactoryTemplateModel(BeanFactory beanFactory,
			ObjectWrapper objectWrapper) {
		
		this.beanFactory = beanFactory;
		this.objectWrapper = objectWrapper;
	}

	public TemplateModel get(String key) throws TemplateModelException {
		Object bean = null;
		try {
			bean = beanFactory.getBean(key);
		}
		catch (BeansException e) {
			throw new TemplateModelException(e);
		}
		return objectWrapper.wrap(bean);
	}

	public boolean isEmpty() throws TemplateModelException {
		return false;
	}

}
