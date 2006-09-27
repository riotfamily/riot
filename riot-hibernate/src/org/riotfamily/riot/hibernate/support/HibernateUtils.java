package org.riotfamily.riot.hibernate.support;

import java.io.Serializable;

import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.riotfamily.common.util.PropertyUtils;

public final class HibernateUtils {
	
	private HibernateUtils() {
	}
	
	public static Object get(Session session, Class beanClass, String id) {
		SessionFactory sf = session.getSessionFactory();
		Serializable serialId = convertId(beanClass, id, sf);
		return session.get(beanClass, serialId);
	}
	
	public static Serializable convertId(Class beanClass, String id, 
			SessionFactory sessionFactory) {
		
		Class identifierClass = sessionFactory.getClassMetadata(beanClass)
				.getIdentifierType().getReturnedClass();
		
		return (Serializable) PropertyUtils.convert(id, identifierClass);
	}

	public static String getIdAsString(SessionFactory sessionFactory, Object bean) {
		Class clazz = Hibernate.getClass(bean);
		ClassMetadata metadata = sessionFactory.getClassMetadata(clazz);
		return metadata.getIdentifier(bean, EntityMode.POJO).toString();
	}
}
