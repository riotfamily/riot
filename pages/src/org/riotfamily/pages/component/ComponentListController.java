package org.riotfamily.pages.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.Cache;
import org.riotfamily.pages.component.context.PageRequestUtils;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.component.impl.AbstractComponent;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.component.render.EditModeRenderStrategy;
import org.riotfamily.pages.component.render.LiveModeRenderStrategy;
import org.riotfamily.pages.component.render.RenderStrategy;
import org.riotfamily.pages.component.resolver.ComponentKeyResolver;
import org.riotfamily.pages.component.resolver.ComponentPathResolver;
import org.riotfamily.pages.component.resolver.FixedComponentKeyResolver;
import org.riotfamily.pages.component.resolver.FixedComponentPathResolver;
import org.riotfamily.pages.component.resolver.TemplateComponentKeyResolver;
import org.riotfamily.pages.component.resolver.UrlComponentPathResolver;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that renders a ComponentList. Which list is to be rendered is 
 * determined using a {@link ComponentPathResolver} and a 
 * {@link ComponentKeyResolver}. 
 */
public class ComponentListController implements Controller,
		BeanNameAware, ComponentListConfiguration {

	private static final ComponentKeyResolver DEFAULT_KEY_RESOLVER = 
			new TemplateComponentKeyResolver();

	private static final ComponentPathResolver DEFAULT_PATH_RESOLVER = 
		new UrlComponentPathResolver();

	
	private Cache cache;

	private ComponentDao componentDao;

	private ComponentKeyResolver componentKeyResolver = DEFAULT_KEY_RESOLVER;

	private ComponentPathResolver componentPathResolver = DEFAULT_PATH_RESOLVER;

	private String[] initialComponentTypes;

	private Integer maxComponents;

	private ComponentRepository repository;

	private String[] validComponentTypes;

	private ViewModeResolver viewModeResolver;

	private String beanName;
	
	public ComponentListController(ComponentDao dao,
			ComponentRepository repository, Cache cache) {

		this.componentDao = dao;
		this.repository = repository;
		this.cache = cache;
	}

	public Cache getCache() {
		return this.cache;
	}

	public ComponentDao getComponentDao() {
		return this.componentDao;
	}

	public ComponentKeyResolver getComponentKeyResolver() {
		return this.componentKeyResolver;
	}

	public ComponentPathResolver getComponentPathResolver() {
		return this.componentPathResolver;
	}

	public String[] getInitialComponentTypes() {
		return this.initialComponentTypes;
	}

	public Integer getMaxComponents() {
		return this.maxComponents;
	}

	public ComponentRepository getRepository() {
		return this.repository;
	}

	public String[] getValidComponentTypes() {
		return this.validComponentTypes;
	}

	public String getBeanName() {
		return this.beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setComponentKey(String key) {
		setComponentKeyResolver(new FixedComponentKeyResolver(key));
	}

	public void setComponentKeyResolver(
			ComponentKeyResolver componentKeyResolver) {
		this.componentKeyResolver = componentKeyResolver;
	}

	public void setComponentPath(String path) {
		setComponentPathResolver(new FixedComponentPathResolver(path));
	}

	public void setComponentPathResolver(
			ComponentPathResolver componentPathResolver) {
		this.componentPathResolver = componentPathResolver;
	}

	public void setInitialComponentTypes(String[] initialComponentTypes) {
		this.initialComponentTypes = initialComponentTypes;
	}

	public void setMaxComponents(Integer maxComponents) {
		this.maxComponents = maxComponents;
	}

	public void setValidComponentTypes(String[] validComponentTypes) {
		this.validComponentTypes = validComponentTypes;
	}

	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}	
	
	public ViewModeResolver getViewModeResolver() {
		return this.viewModeResolver;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		boolean preview = viewModeResolver.isPreviewMode(request);
		RenderStrategy strategy = null;
		if (preview) {
			strategy = new EditModeRenderStrategy(this, request, response);
			PageRequestUtils.storeContext(request, 120000);
		}
		else {
			strategy = new LiveModeRenderStrategy(this, request, response);
		}
		
		String path = getComponentPath(request);
		String key = getComponentKey(request);
		strategy.render(path, key);
		
		return null;
	}

	protected String getComponentPath(HttpServletRequest request) {
		VersionContainer parentContainer = (VersionContainer) 
				request.getAttribute(AbstractComponent.CONTAINER);
		
		if (parentContainer != null) {
			return parentContainer.getList().getPath();
		}
		return componentPathResolver.getComponentPath(request);
	}
	
	protected String getComponentKey(HttpServletRequest request) {
		VersionContainer parentContainer = (VersionContainer) 
				request.getAttribute(AbstractComponent.CONTAINER);
		
		if (parentContainer != null) {
			return parentContainer.getList().getKey() + "$" 
					+ parentContainer.getId();
		}
		return componentKeyResolver.getComponentKey(request);
	}

}
