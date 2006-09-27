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
