package org.riotfamily.pages.component;

import org.riotfamily.cachius.Cache;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.component.resolver.ComponentPathResolver;

public interface ComponentListConfiguration {

	public ViewModeResolver getViewModeResolver();
	
	public ComponentPathResolver getComponentPathResolver();
	
	public Cache getCache();
	
	public Integer getMaxComponents();
	
	public String[] getValidComponentTypes();
	
	public String[] getInitialComponentTypes();
	
	public ComponentDao getComponentDao();
	
	public ComponentRepository getRepository();

	public String getBeanName();

}
