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
package org.riotfamily.forms;

import org.riotfamily.common.beans.ProtectedBeanWrapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class BeanEditorBinder extends AbstractEditorBinder {

	private BeanWrapper beanWrapper;
	
	private boolean editingExistingBean;
	
	public BeanEditorBinder(Object backingObject) {
		Assert.notNull(backingObject);
		beanWrapper = new ProtectedBeanWrapper(backingObject);
		editingExistingBean = true;
	}
	
	public BeanEditorBinder(Class beanClass) {
		beanWrapper = new ProtectedBeanWrapper(beanClass);
	}

	public Object getBackingObject() {
		return beanWrapper.getWrappedInstance();
	}

	public void setBackingObject(Object backingObject) {
		editingExistingBean = backingObject != null;
		if (backingObject != null) {
			beanWrapper.setWrappedInstance(backingObject);
		}
		else {
			beanWrapper = new ProtectedBeanWrapper(getBeanClass());
		}
	}
	
	public boolean isEditingExistingBean() {
		return editingExistingBean;
	}
	
	public Class getBeanClass() {
		return beanWrapper.getWrappedClass();
	}

	public Class getPropertyType(String propertyName) {
		return beanWrapper.getPropertyType(propertyName);
	}
	
	public Object getPropertyValue(String propertyName) {
		return beanWrapper.getPropertyValue(propertyName);
	}

	public void setPropertyValue(String propertyName, Object value) {
		beanWrapper.setPropertyValue(propertyName, value);
	}

}
