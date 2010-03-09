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
package org.riotfamily.common.web.mvc.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.OrderComparator;
import org.springframework.util.Assert;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

/**
 * Helper class that resolves views the same way as a {@link DispatcherServlet}.
 */
public class ViewResolverHelper {

    private ArrayList<ViewResolver> viewResolvers;

    private RequestToViewNameTranslator viewNameTranslator;

	public ViewResolverHelper(ListableBeanFactory beanFactory) {
		Map<String, ViewResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				beanFactory, ViewResolver.class, true, false);

        if (!matchingBeans.isEmpty()) {
            this.viewResolvers = new ArrayList<ViewResolver>(matchingBeans.values());
            Collections.sort(this.viewResolvers, new OrderComparator());
        }

        try {
			this.viewNameTranslator = beanFactory.getBean(
					DispatcherServlet.REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME,
					RequestToViewNameTranslator.class);
		}
		catch (NoSuchBeanDefinitionException ex) {
			this.viewNameTranslator = new DefaultRequestToViewNameTranslator();
		}
	}

	public View resolveView(HttpServletRequest request, ModelAndView mv)
			throws ViewResolutionException {

		if (mv.hasView() && !mv.isReference()) {
		    return mv.getView();
		}
		return resolveView(request, mv.getViewName());
	}

	public View resolveView(HttpServletRequest request, String viewName)
			throws ViewResolutionException {

		if (viewName == null) {
			try {
				viewName = viewNameTranslator.getViewName(request);
			}
			catch (Exception e) {
				throw new ViewResolutionException(null, e);
			}
		}
		Locale locale = RequestContextUtils.getLocale(request);
		return resolveView(locale, viewName);
	}
	
	public View resolveView(Locale locale, String viewName)
			throws ViewResolutionException {

		Assert.notNull(viewName, "viewName must not be null");
		try {
			for (ViewResolver viewResolver : viewResolvers) {
			    View view = viewResolver.resolveViewName(viewName, locale);
			    if (view != null) {
			        return view;
			    }
			}
		}
		catch (Exception e) {
			throw new ViewResolutionException(viewName, e);
		}
		throw new ViewResolutionException(viewName);
	}

}
