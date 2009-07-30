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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
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
