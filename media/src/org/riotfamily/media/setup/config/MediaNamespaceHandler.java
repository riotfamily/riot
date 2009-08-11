package org.riotfamily.media.setup.config;

import org.riotfamily.common.beans.namespace.GenericNamespaceHandlerSupport;
import org.riotfamily.media.setup.RiotFileFactoryBean;
import org.riotfamily.media.setup.RiotImageFactoryBean;
import org.riotfamily.media.setup.RiotSwfFactoryBean;
import org.riotfamily.media.setup.RiotVideoFactoryBean;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class MediaNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("file", RiotFileFactoryBean.class)
				.addReference("processor");
		
		register("image", RiotImageFactoryBean.class)
				.addReference("processor");
		
		register("swf", RiotSwfFactoryBean.class)
				.addReference("processor");
		
		register("video", RiotVideoFactoryBean.class)
				.addReference("processor");
	}
	
}
