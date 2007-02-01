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
package org.riotfamily.pages.component.dao;

import java.util.Collection;

import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.setup.Plumber;
import org.riotfamily.pages.setup.WebsiteConfig;
import org.riotfamily.pages.setup.WebsiteConfigAware;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.support.RiotDaoAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;

/**
 * @author flx
 * @since 6.4
 */
public class DirtyComponentListDao extends RiotDaoAdapter 
		implements ApplicationContextAware, WebsiteConfigAware {

	private ComponentDao componentDao;
	
	public final void setApplicationContext(ApplicationContext context) {
		Plumber.register(context, this);
	}
	
	public void setWebsiteConfig(WebsiteConfig config) {
		componentDao = config.getComponentDao();
	}
	
	public Collection list(Object parent, ListParams params) 
			throws DataAccessException {
		
		return componentDao.findDirtyComponentLists();
	}
	
	public String getObjectId(Object entity) {
		return ((ComponentList) entity).getId().toString();
	}
	
	public Object load(String id) throws DataAccessException {
		return componentDao.loadComponentList(new Long(id));
	}
}
