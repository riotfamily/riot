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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.riotfamily.common.collection.TypeDifferenceComparator;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.MethodParameter;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class HibernateWrapperFactory implements ValueWrapperFactory {

	private static Log log = LogFactory.getLog(HibernateWrapperFactory.class);
	
	private ArrayList contentClassInfos = new ArrayList();
	
	private ArrayList collectionClassInfos = new ArrayList();
	
	public HibernateWrapperFactory(SessionFactory sessionFactory) {
		Iterator it = sessionFactory.getAllClassMetadata().values().iterator();
		while (it.hasNext()) {
			ClassMetadata meta = (ClassMetadata) it.next();
			Class entityClass = meta.getMappedClass(EntityMode.POJO);
			if (ValueWrapper.class.isAssignableFrom(entityClass)) {
				Constructor[] ctors = entityClass.getConstructors();
				for (int i = 0; i < ctors.length; i++) {
					Class[] params = ctors[i].getParameterTypes();
					if (params.length == 1) {
						registerContentClass(params[0], ctors[i]);
					}
				}
			}
		}
	}
	
	private void registerContentClass(Class contentType, Constructor ctor) {
		Class collectionType = getCollectionType(contentType, ctor); 
		if (collectionType != null) {
			log.debug("Registering " + ctor.getName() 
					+ " as Content class for Collection<" 
					+ collectionType + ">");
			
			collectionClassInfos.add(new ContentClassInfo(collectionType, ctor));
		}
		else {
			log.debug("Registering " + ctor.getName() 
					+ " as Content class for " + contentType);
			
			contentClassInfos.add(new ContentClassInfo(contentType, ctor));
		}
	}
	
	private Class getCollectionType(Class contentType, Constructor ctor) {
		if (Collection.class.isAssignableFrom(contentType)) {
			return GenericCollectionTypeResolver.getCollectionParameterType(
					new MethodParameter(ctor, 0));
		}
		return null;
	}
		
	private Class getCollectionType(Object value) {
		if (value instanceof Collection) {
			return GenericCollectionTypeResolver.getCollectionType(value.getClass());
		}
		return null;
	}
	
	private ContentClassInfo getContentClassInfo(List classInfos, Class contentType) {
		TreeSet infos = new TreeSet(new ContentClassInfoComparator(contentType));
		infos.addAll(classInfos);
		return (ContentClassInfo) infos.first();
	}
	
	public ValueWrapper createWapper(Object value) throws ContentException {
		Class collectionType = getCollectionType(value);
		ContentClassInfo info;
		if (collectionType != null) {
			info = getContentClassInfo(collectionClassInfos, collectionType);
		}
		else {
			info = getContentClassInfo(contentClassInfos, value.getClass());
		}
		return info.createContent(value);
	}
	
	private static class ContentClassInfo {
		
		private Class contentType;
		
		private Constructor constructor;

		public ContentClassInfo(Class contentType,  Constructor constructor) {
			this.contentType = contentType;
			this.constructor = constructor;
		}
		
		private ValueWrapper createContent(Object value) throws ContentException {
			try {
				log.debug("Creating new " + constructor.getName() + " for " + value);
				return (ValueWrapper) constructor.newInstance(new Object[] {value});
			}
			catch (IllegalArgumentException e) {
				throw new ContentException(e);
			}
			catch (InstantiationException e) {
				throw new ContentException(e);
			}
			catch (IllegalAccessException e) {
				throw new ContentException(e);
			}
			catch (InvocationTargetException e) {
				throw new ContentException(e);
			}
		}
	}
	
	private static class ContentClassInfoComparator
			extends TypeDifferenceComparator {
	
		public ContentClassInfoComparator(Class contentType) {
			super(contentType);
		}
		
		public int compare(Object o1, Object o2) {
			ContentClassInfo cc1 = (ContentClassInfo) o1;
			ContentClassInfo cc2 = (ContentClassInfo) o2;
			return super.compare(cc1.contentType, cc2.contentType);
		}
	}
}
