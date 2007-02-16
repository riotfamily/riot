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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.setup;

import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;


/**
 * Bean that sets a reference to the WebsiteConfig bean defined in the
 * website-servlet's context on all beans that implement the WebsiteConfigAware
 * interface. 
 * <p>
 * Please refer to the package description for more information.
 * </P>
 * Usage: Place an instance of this class in your application context.
 */
public class Plumber {

	private ArrayList configAwareBeans = new ArrayList();
	
	private WebsiteConfig websiteConfig;

	public void setWebsiteConfig(WebsiteConfig websiteConfig) {
		this.websiteConfig = websiteConfig;
		Iterator it = configAwareBeans.iterator();
		while (it.hasNext()) {
			WebsiteConfigAware bean = (WebsiteConfigAware) it.next();
			bean.setWebsiteConfig(websiteConfig);
		}
	}
	
	public void register(WebsiteConfigAware bean) {
		configAwareBeans.add(bean);
		if (websiteConfig != null) {
			bean.setWebsiteConfig(websiteConfig);
		}
	}
	
	public static void register(ApplicationContext context, 
			WebsiteConfigAware bean) {
		
		Plumber plumber = (Plumber) BeanFactoryUtils.beanOfType(
				context.getParent(), Plumber.class);
		
		plumber.register(bean);
	}

}
