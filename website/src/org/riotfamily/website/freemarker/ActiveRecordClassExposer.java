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
package org.riotfamily.website.freemarker;

import java.io.IOException;
import java.util.Collection;

import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.web.view.freemarker.ConfigurationPostProcessor;
import org.riotfamily.riot.hibernate.domain.ActiveRecord;
import org.springframework.util.ClassUtils;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;

/**
 * ConfigurationPostProcessor that adds static models for all ActiveRecord 
 * classes under their {@link ClassUtils#getShortName(Class) short name}
 * as shared variables.
 * <p>
 * If two ActiveRecord classes with the same name exist in different packages
 * only the first one will be exposed and a warning message will be logged.
 * In order to access the other (shadowed) class you can use the following 
 * syntax in your FreeMarker templates:
 * <code>statics["com.example.MyActiveRecord"]</code>.
 * </p>
 * 
 * @see ActiveRecord
 * @see BeansWrapper#getStaticModels()
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class ActiveRecordClassExposer implements ConfigurationPostProcessor {

	RiotLog log = RiotLog.get(this);
	
	private SessionFactory sessionFactory;
	
	public ActiveRecordClassExposer(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private BeansWrapper getBeansWrapper(Configuration config) {
		if (config.getObjectWrapper() instanceof BeansWrapper) {
			return (BeansWrapper) config.getObjectWrapper();  
		}
		return BeansWrapper.getDefaultInstance();
	}
	
	@SuppressWarnings("unchecked")
	public void postProcessConfiguration(Configuration config)
			throws IOException, TemplateException {
		
		TemplateHashModel statics = getBeansWrapper(config).getStaticModels();
		Collection<ClassMetadata> allMetadata = sessionFactory.getAllClassMetadata().values();
		for (ClassMetadata meta : allMetadata) {
			Class mappedClass = meta.getMappedClass(EntityMode.POJO);
			if (ActiveRecord.class.isAssignableFrom(mappedClass)) {
				String key = ClassUtils.getShortName(mappedClass);
				if (config.getSharedVariable(key) != null) {
					log.warn("Another shared variable with the name '%s'" +
							" already exist. Use statics[\"%s\"] in your" +
							" FreeMarker templates to access the static" +
							" methods of your ActiveRecord class.", 
							key, mappedClass.getName());
				}
				else {
					TemplateModel tm = statics.get(mappedClass.getName());
					config.setSharedVariable(key, tm);
				}
			}
		}
	}

}
