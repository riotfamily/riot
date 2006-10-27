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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.dao.support;

import java.lang.reflect.Method;
import java.util.Collection;

import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.dao.ListParams;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class CustomDaoAdpater implements RiotDao, InitializingBean {

	public static final String DEFAULT_ID_PROPERTY = "id";
	
	private Object customDao;
	
	private Class itemClass;
	
	private String idProperty = DEFAULT_ID_PROPERTY;
	
	private String listMethodName;
	
	private String loadMethodName;
	
	private String saveMethodName;
	
	private String updateMethodName;
	
	private String deleteMethodName;
	
	
	private Class idClass;
	
	private Method listMethod;
	
	private Method loadMethod;
	
	private Method saveMethod;
	
	private Method updateMethod;
	
	private Method deleteMethod;

	
	public CustomDaoAdpater(Object customDao, Class itemClass) {
		this.customDao = customDao;
		this.itemClass = itemClass;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public void setListMethod(String listMethodName) {
		this.listMethodName = listMethodName;
	}

	public void setLoadMethod(String loadMethodName) {
		this.loadMethodName = loadMethodName;
	}

	public void setSaveMethod(String saveMethodName) {
		this.saveMethodName = saveMethodName;
	}

	public void setUpdateMethod(String updateMethodName) {
		this.updateMethodName = updateMethodName;
	}
	
	public void setDeleteMethod(String deleteMethodName) {
		this.deleteMethodName = deleteMethodName;
	}

	public void afterPropertiesSet() throws Exception {
		idClass = PropertyUtils.getPropertyType(itemClass, idProperty);
		Class daoClass = customDao.getClass();

		Assert.notNull(listMethodName, "A listMethod must be specified.");
		listMethod = daoClass.getMethod(listMethodName, null);
		
		Assert.notNull(loadMethodName, "A loadMethod must be specified.");
		loadMethod = daoClass.getMethod(loadMethodName, new Class[] { idClass });
		
		if (saveMethodName != null) {
			saveMethod = daoClass.getMethod(saveMethodName, new Class[] { itemClass });
		}
		
		if (updateMethodName != null) {
			updateMethod = daoClass.getMethod(updateMethodName, new Class[] { itemClass });
		}
		
		if (deleteMethodName != null) {
			deleteMethod = daoClass.getMethod(deleteMethodName, new Class[] { itemClass });
		}
	}
		
	public Class getEntityClass() {
		return itemClass;
	}
	
	public Collection list(Object parent, ListParams params) {
		return (Collection) ReflectionUtils.invokeMethod(listMethod, customDao, null);
	}

	public String getObjectId(Object item) {
		return PropertyUtils.getPropertyAsString(item, idProperty);
	}

	public int getListSize(Object parent, ListParams params) {
		return -1;
	}

	public Object load(String objectId) {
		Object id = PropertyUtils.convert(objectId, idClass);
		return ReflectionUtils.invokeMethod(loadMethod, customDao, new Object[] { id });
	}

	public void save(Object item, Object parent) {
		Assert.notNull(saveMethod, "A saveMethod must be specified.");
		ReflectionUtils.invokeMethod(saveMethod, customDao, new Object[] { item });
	}

	public void update(Object item) {
		Assert.notNull(saveMethod, "An updateMethod must be specified.");
		ReflectionUtils.invokeMethod(updateMethod, customDao, new Object[] { item });
	}
	
	public void delete(Object item, Object parent) {
		Assert.notNull(saveMethod, "A deleteMethod must be specified.");
		ReflectionUtils.invokeMethod(deleteMethod, customDao, new Object[] { item });
	}

}
