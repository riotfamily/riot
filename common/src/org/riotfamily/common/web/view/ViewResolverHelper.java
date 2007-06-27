/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.OrderComparator;
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

    private ArrayList viewResolvers;

    private RequestToViewNameTranslator viewNameTranslator;

	public ViewResolverHelper(ListableBeanFactory beanFactory) {
		Map matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				beanFactory, ViewResolver.class, true, false);

        if (!matchingBeans.isEmpty()) {
            this.viewResolvers = new ArrayList(matchingBeans.values());
            Collections.sort(this.viewResolvers, new OrderComparator());
        }

        try {
			this.viewNameTranslator = (RequestToViewNameTranslator) beanFactory.getBean(
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

		try {
			if (viewName == null) {
				viewName = viewNameTranslator.getViewName(request);
			}
			Iterator i = viewResolvers.iterator();
			while (i.hasNext()) {
			    ViewResolver viewResolver = (ViewResolver) i.next();
			    Locale locale = RequestContextUtils.getLocale(request);
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
