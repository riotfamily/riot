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
package org.riotfamily.common.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.io.Resource;

/**
 *
 */
public class BeanConfigurationWatcher {

	private ConfigurableBean bean;
	
	private List resources;
	
	private long lastModified;
	
	private ArrayList listeners = new ArrayList();
	
	public BeanConfigurationWatcher(ConfigurableBean bean) {
		this.bean = bean;
		this.lastModified = System.currentTimeMillis();
	}

	public void setResources(List resources) {
		this.resources = resources;
	}

	public void addListener(ConfigurationEventListener listener) {
		listeners.add(listener);
	}
	
	public synchronized void checkForModifications() {
		if (bean.isReloadable()) {
			long mtime = 0;
			Iterator it = resources.iterator();
			while (it.hasNext()) {
				Resource res = (Resource) it.next();
				try {
					File f = res.getFile();
					mtime = Math.max(mtime, f.lastModified());
				}
				catch (IOException e) {
				}
			}
			if (mtime > lastModified) {
				lastModified = System.currentTimeMillis();
				bean.configure();
				it = listeners.iterator();
				while (it.hasNext()) {
					ConfigurationEventListener listener = (ConfigurationEventListener) it.next();
					listener.beanReconfigured(bean);
				}
			}
		}
	}
	
}
