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
package org.riotfamily.components.model.wrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.riotfamily.common.collection.TypeDifferenceComparator;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class HibernateWrapperFactory implements ValueWrapperFactory {

	private static Log log = LogFactory.getLog(HibernateWrapperFactory.class);
	
	private ArrayList wrapperClassInfos = new ArrayList();
		
	public HibernateWrapperFactory(SessionFactory sessionFactory) {
		Iterator it = sessionFactory.getAllClassMetadata().values().iterator();
		while (it.hasNext()) {
			ClassMetadata meta = (ClassMetadata) it.next();
			Class wrapperClass = meta.getMappedClass(EntityMode.POJO);
			if (ValueWrapper.class.isAssignableFrom(wrapperClass)) {
				String[] properties = meta.getPropertyNames();
				if (properties.length > 0) {
					String valueProperty = properties[0];
					Class valueClass = meta.getPropertyType(valueProperty).getReturnedClass();
					log.debug("Registering " + wrapperClass	+ " as wrapper for " + valueClass);
					wrapperClassInfos.add(new WrapperClassInfo(wrapperClass, valueClass, valueProperty));
				}
			}
		}
	}
	
	private WrapperClassInfo getWrapperClassInfo(Class valueClass) {
		TreeSet infos = new TreeSet(new WrapperClassInfoComparator(valueClass));
		infos.addAll(wrapperClassInfos);
		WrapperClassInfo bestMatch = (WrapperClassInfo) infos.first();
		if (bestMatch.getValueClass().isAssignableFrom(valueClass)) {
			return bestMatch;	
		}
		return null;
	}
	
	public ValueWrapper createWapper(Object value) throws WrappingException {
		WrapperClassInfo info = getWrapperClassInfo(value.getClass());
		if (info == null) {
			throw new WrappingException("No ValueWrapper found for type " 
					+ value.getClass());
		}
		return info.createWrapper(value);
	}
	
	private static class WrapperClassInfo {
		
		private Class wrapperClass;
		
		private Class valueClass;
		
		private String valueProperty;

		public WrapperClassInfo(Class wrapperClass, Class valueClass, 
				String valueProperty) {
			
			this.wrapperClass = wrapperClass;
			this.valueClass = valueClass;
			this.valueProperty = valueProperty;
		}

		public Class getWrapperClass() {
			return this.wrapperClass;
		}

		public Class getValueClass() {
			return this.valueClass;
		}

		public String getValueProperty() {
			return this.valueProperty;
		}

		public ValueWrapper createWrapper(Object value) throws WrappingException {
			try {
				ValueWrapper wrapper = (ValueWrapper) BeanUtils.instantiateClass(wrapperClass);
				wrapper.wrap(value);
				return wrapper;
			}
			catch (BeanInstantiationException e) {
				throw new WrappingException(e);
			}
		}
	}
	
	private static class WrapperClassInfoComparator
			extends TypeDifferenceComparator {
	
		public WrapperClassInfoComparator(Class contentType) {
			super(contentType);
		}
		
		public int compare(Object o1, Object o2) {
			WrapperClassInfo cc1 = (WrapperClassInfo) o1;
			WrapperClassInfo cc2 = (WrapperClassInfo) o2;
			return super.compare(cc1.getValueClass(), cc2.getValueClass());
		}
	}
}
