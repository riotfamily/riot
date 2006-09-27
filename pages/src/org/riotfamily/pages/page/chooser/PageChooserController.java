package org.riotfamily.pages.page.chooser;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.pages.page.menu.PageSitemapBuilder;
import org.riotfamily.pages.setup.WebsiteConfigSupport;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class PageChooserController extends WebsiteConfigSupport 
		implements Controller, UrlMappingAware, BeanNameAware {

	
	private PageSitemapBuilder sitemapBuilder = new PageSitemapBuilder();
	
	private UrlMapping urlMapping;
	
	private String beanName;

	private String viewName = ResourceUtils.getPath(
			PageChooserController.class, "PageChooserView.ftl");
		
	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
		
	public String getUrl() {
		return urlMapping.getUrl(beanName, null);
	}
			
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		List items = sitemapBuilder.createItems(
				getPageMap().getRootPages(), request);
		
		return new ModelAndView(viewName, "items", items);
	}

}
