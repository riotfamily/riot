package org.riotfamily.common.xml;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;

/**
 * class to configure a bean using a DocumentDigester and a XML resource.
 */
public class XmlBeanConfigurer {

	private Log log = LogFactory.getLog(XmlBeanConfigurer.class);
	
	private ConfigurableBean bean;
	
	private List resources;
	
	private DocumentDigester digester;
	
	private ApplicationEventPublisher eventPublisher;
	
	private long lastModified;
	
	private boolean configuring;
	
	public XmlBeanConfigurer(ConfigurableBean bean, List resources, 
			DocumentDigester digester) {
		
		this(bean, resources, digester, null);
	}
	
	public XmlBeanConfigurer(ConfigurableBean bean, List resources, 
			DocumentDigester digester, ApplicationEventPublisher eventPublisher) {
		
		this.bean = bean;
		this.resources = resources;
		this.digester = digester;
		this.eventPublisher = eventPublisher;
	}
	
	public void configure() {
		synchronized (bean) {
			lastModified = System.currentTimeMillis();
			Iterator it = resources.iterator();
			while (it.hasNext()) {
				Resource res = (Resource) it.next();
				DocumentReader reader = new ValidatingDocumentReader(res);
				log.debug("Digesting config file: " + res);
				digester.digest(reader.readDocument(), res);
			}
			bean.configured();
		}
	}
	
	public void checkForModifications() {
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
				synchronized (bean) {
					reconfigure();
				}
			}
		}
	}
	
	public synchronized void reconfigure() {
		if (!configuring) {
			try {
				configuring = true;
				bean.reset();
				configure();
				if (eventPublisher != null) {
					eventPublisher.publishEvent(
							new ConfigurationReloadedEvent(bean));
				}
			}
			finally {
				configuring = false;
			}
		}
	}

}
