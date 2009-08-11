package org.riotfamily.common.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;

/**
 *
 */
public class BeanConfigurationWatcher {

	private ConfigurableBean bean;
	
	private List<File> files;
	
	private long lastModified;
	
	private ArrayList<ConfigurationEventListener> listeners = 
			new ArrayList<ConfigurationEventListener>();
	
	public BeanConfigurationWatcher(ConfigurableBean bean) {
		this.bean = bean;
		this.lastModified = System.currentTimeMillis();
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public void setResources(List<Resource> resources) {
		files = new ArrayList<File>();
		for (Resource res : resources) {
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
			for (File file : files) {
				mtime = Math.max(mtime, file.lastModified());
			}
			if (mtime > lastModified) {
				lastModified = mtime;
				bean.configure();
				for (ConfigurationEventListener listener : listeners) {
					listener.beanReconfigured(bean);
				}
			}
		}
	}
	
}
