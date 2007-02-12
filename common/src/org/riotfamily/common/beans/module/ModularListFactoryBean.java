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
package org.riotfamily.common.beans.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Creates a list by merging the entries with the given key from all
 * {@link org.riotfamily.common.beans.module.FactoryBeanModule modules} found in
 * the ApplicationContext.
 * 
 * @see org.riotfamily.common.beans.module.FactoryBeanModule
 */
public class ModularListFactoryBean extends AbstractFactoryBean implements
		ApplicationContextAware {

	private List sourceList;

	private boolean sourceListFirst = false;

	private boolean includeRootList = false;

	private Class targetListClass = ArrayList.class;

	private String key;

	private ApplicationContext applicationContext;

	private static Log log = LogFactory.getLog(ModularListFactoryBean.class);

	/**
	 * Set the source List, typically populated via XML "list" elements.
	 */
	public void setSourceList(List sourceList) {
		this.sourceList = sourceList;
	}

	/**
	 * Sets whether items from the source list should be inserted before items
	 * provided by modules. Default is false.
	 * 
	 * @since 6.4
	 */

	public void setSourceListFirst(boolean sourceListFirst) {
		this.sourceListFirst = sourceListFirst;
	}

	/**
	 * Sets whether the root bean defintion (having the key as id) should be
	 * included in the list. This is useful if not only modules but the
	 * application itself should be able to add items to the list.
	 */
	public void setIncludeRootList(boolean includeRootList) {
		this.includeRootList = includeRootList;
	}

	/**
	 * Set the class to use for the target List. Can be populated with a fully
	 * qualified class name when defined in a Spring application context.
	 * <p>
	 * Default is a <code>java.util.ArrayList</code>.
	 * 
	 * @see java.util.ArrayList
	 */
	public void setTargetListClass(Class targetListClass) {
		if (targetListClass == null) {
			throw new IllegalArgumentException(
					"targetListClass must not be null");
		}
		if (!List.class.isAssignableFrom(targetListClass)) {
			throw new IllegalArgumentException(
					"targetListClass must implement [java.util.List]");
		}
		this.targetListClass = targetListClass;
	}

	public Class getObjectType() {
		return java.util.List.class;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected Object createInstance() {
		List result = (List) BeanUtils.instantiateClass(this.targetListClass);

		if (sourceListFirst && sourceList != null) {
			result.addAll(sourceList);
		}

		Collection modules = ModularFactoryBeansUtils
				.getFactoryBeanModules(applicationContext);

		Iterator it = modules.iterator();
		while (it.hasNext()) {
			FactoryBeanModule module = (FactoryBeanModule) it.next();
			List moduleList = module.getList(key);
			if (moduleList != null) {
				log.info("Adding items defined by " + module.getName() + " to "
						+ key);

				result.addAll(moduleList);
			}
		}

		if (!sourceListFirst && sourceList != null) {
			result.addAll(sourceList);
		}

		if (includeRootList) {
			try {
				List rootList = (List) applicationContext.getBean(key, List.class);
				result.add(rootList);
			}
			catch (NoSuchBeanDefinitionException e) {
			}
		}

		return result;
	}
}
