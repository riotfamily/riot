package org.riotfamily.common.view;

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

	@SuppressWarnings("unchecked")
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

		if (viewName == null) {
			try {
				viewName = viewNameTranslator.getViewName(request);
			}
			catch (Exception e) {
				throw new ViewResolutionException(viewName, e);
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
