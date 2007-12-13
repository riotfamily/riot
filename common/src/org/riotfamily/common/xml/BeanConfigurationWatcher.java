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
	
	private List files;
	
	private long lastModified;
	
	private ArrayList listeners = new ArrayList();
	
	public BeanConfigurationWatcher(ConfigurableBean bean) {
		this.bean = bean;
		this.lastModified = System.currentTimeMillis();
	}

	public void setFiles(List files) {
		this.files = files;
	}

	public void setResources(List resources) {
		files = new ArrayList();
		Iterator it = resources.iterator();
		while (it.hasNext()) {
			Resource res = (Resource) it.next();
			try {
				files.add(res.getFile());
			}
			catch (IOException e) {
			}
		}
	}

	public void addListener(ConfigurationEventListener listener) {
		listeners.add(listener);
	}
	
	public synchronized void checkForModifications() {
		if (bean.isReloadable()) {
			long mtime = 0;
			Iterator it = files.iterator();
			while (it.hasNext()) {
				File file = (File) it.next();
				mtime = Math.max(mtime, file.lastModified());
			}
			if (mtime > lastModified) {
				lastModified = mtime;
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
