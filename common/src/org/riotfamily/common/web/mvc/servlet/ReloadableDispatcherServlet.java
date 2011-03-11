/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.web.mvc.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.beans.reload.BeanConfigurationWatcher;
import org.riotfamily.common.beans.reload.ConfigurableBean;
import org.riotfamily.common.web.support.ServletUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

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
public class ReloadableDispatcherServlet extends InterceptingDispatcherServlet
		implements ConfigurableBean {

	private boolean reloadable = true;

	private BeanConfigurationWatcher watcher = new BeanConfigurationWatcher(this);

	@Override
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
					BeanFactoryUtils.beanOfType(context,
			ReloadableDispatcherServletConfig.class);

			setReloadable(config.isReloadable());
		}
		catch (NoSuchBeanDefinitionException e) {
		}
	}

	@Override
	protected void onRefresh(ApplicationContext context) throws BeansException {
		super.onRefresh(context);
		configureFromContext(context);
		ResourceAwareContext ctx = (ResourceAwareContext) context;
		watcher.setResources(ctx.getConfigResources());
	}

	@Override
	protected void doDispatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		watcher.checkForModifications();
		super.doDispatch(request, response);
	}
	
	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (ServletUtils.isDirectRequest(request)) {
			super.doHead(request, response);
		} else {
			doGet(request, response);
		}
	}

	public void configure() {
		refresh();
	}
	
}
