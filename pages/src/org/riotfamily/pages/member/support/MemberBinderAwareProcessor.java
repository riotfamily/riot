package org.riotfamily.pages.member.support;

import org.riotfamily.pages.member.MemberBinder;
import org.riotfamily.pages.member.MemberBinderAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MemberBinderAwareProcessor implements BeanPostProcessor {

	private MemberBinder memberBinder;
	
	public MemberBinderAwareProcessor(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) 
			throws BeansException {
		
		if (bean instanceof MemberBinderAware) {
			((MemberBinderAware) bean).setMemberBinder(memberBinder);
		}
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) 
			throws BeansException {
		
		return bean;
	}

}
