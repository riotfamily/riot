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
