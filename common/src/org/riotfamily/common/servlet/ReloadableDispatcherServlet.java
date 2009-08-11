package org.riotfamily.common.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.xml.BeanConfigurationWatcher;
import org.riotfamily.common.xml.ConfigurableBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * DispatcherServlet that checks whether one of the configuration files has
 * been modified. If a change is detected the servlet is re-initalized and the
 * underlying BeanFactory is refreshed.
 * <p>
 * As checks are performed upon each request you might want to set the
 * <code>reloadable</code> init parameter to <code>false</code> when used in
 * a production environment. Alternatively you can add a
 * {@link ReloadableDispatcherServletConfig} bean to your ApplicationContext
 * which allows you to set the <code>reloadable</code> property without
 * modifying the web.xml.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 */
public class ReloadableDispatcherServlet extends DispatcherServlet
		implements ConfigurableBean {

	private boolean reloadable = true;

	private BeanConfigurationWatcher watcher = new BeanConfigurationWatcher(this);

	public Class<? extends ApplicationContext> getContextClass() {
		return ResourceAwareContext.class;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	public boolean isReloadable() {
		return reloadable;
	}

	private void configureFromContext(ApplicationContext context) {
		try {
			ReloadableDispatcherServletConfig config =
					(ReloadableDispatcherServletConfig)
					BeanFactoryUtils.beanOfType(context,
					ReloadableDispatcherServletConfig.class);

			setReloadable(config.isReloadable());
		}
		catch (NoSuchBeanDefinitionException e) {
		}
	}

	protected void onRefresh(ApplicationContext context) throws BeansException {
		super.onRefresh(context);
		configureFromContext(context);
		ResourceAwareContext ctx = (ResourceAwareContext) context;
		watcher.setResources(ctx.getConfigResources());
	}

	protected void doDispatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		watcher.checkForModifications();
		super.doDispatch(request, response);
	}

	public void configure() {
		refresh();
	}
	
	/**
	 * 
	 */
	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		if (ServletUtils.isDirectRequest(request)) {
			super.doHead(request, response);
		}
		else {
			doGet(request, response);
		}
	}
}
