package org.riotfamily.common.web.view.freemarker;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.view.ViewContext;

public class ExternalUrlMethod extends AbstractSimpleMethod {

	private Log log = LogFactory.getLog(ExternalUrlMethod.class);
	
	protected Object exec(Object arg) throws Exception {
		return Boolean.valueOf(isExternalUrl((String) arg));
	}

	protected boolean isExternalUrl(String url) {
		try {
			URI uri = new URI(url);
			if (!uri.isOpaque()) {
				if (uri.isAbsolute() && !isSameHost(uri.getHost())) {
					return true;
				}
			}
		}
		catch (Exception e) {
			log.warn(e.getMessage());
		}
		return false;
	}
	
	protected boolean isSameHost(String host) {
		return ViewContext.getRequest().getServerName().equals(host);
	}
}
