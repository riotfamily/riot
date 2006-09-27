package org.riotfamily.cachius.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.OrderComparator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Helper class that resolves views the same way as a DispatcherServler.
 */
public class ViewResolverHelper {

    private ArrayList viewResolvers;
    
	public ViewResolverHelper(ListableBeanFactory beanFactory) {
		Map matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				beanFactory, ViewResolver.class, true, false);

        if (!matchingBeans.isEmpty()) {
            this.viewResolvers = new ArrayList(matchingBeans.values());
            Collections.sort(this.viewResolvers, new OrderComparator());
        }
	}
	
	public View resolveView(HttpServletRequest request, ModelAndView mv) 
			throws Exception {

		if (!mv.isReference()) {
		    return mv.getView();
		}
		String viewName = mv.getViewName();
		View view = null;
		Iterator i = viewResolvers.iterator();
		while (i.hasNext()) {
		    ViewResolver viewResolver = (ViewResolver) i.next();
		    Locale locale = RequestContextUtils.getLocale(request);
		    view = viewResolver.resolveViewName(viewName, locale);
		    if (view != null) {
		        return view;
		    }
		}
		throw new Exception("Could not resolve view with name " + viewName);
	}

}
