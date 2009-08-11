package org.riotfamily.components.render.component;

import org.riotfamily.common.view.ViewResolverHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ViewComponentRendererInitializer implements ApplicationContextAware {

	private ViewComponentRenderer renderer;

	public ViewComponentRendererInitializer(ViewComponentRenderer renderer) {
		this.renderer = renderer;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		renderer.setViewResolverHelper(new ViewResolverHelper(applicationContext));		
	}
}
