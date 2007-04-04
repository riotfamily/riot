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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.mvc.view;

import java.util.Locale;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * ViewResolver that returns a {@link DumpModelView} if the the viewName 
 * equals the {@link #setDumpViewName(String) set dumpViewName}.
 * <p>
 * In order to dump the model whenever a controller returns no view, add the
 * following to bean definitions to the context of your DispatcherServlet: 
 * <pre>
 *  &lt;bean id="viewNameTranslator" class="{@link org.riotfamily.website.mvc.view.FixedViewNameTranslator}" /&gt;
 *  &lt;bean class="{@link org.riotfamily.website.mvc.view.DumpModelViewResolver}" /&gt;
 * </pre>	
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class DumpModelViewResolver implements ViewResolver, Ordered {

	private DumpModelView dumpModelView = new DumpModelView();

	private String dumpViewName = FixedViewNameTranslator.DEFAULT_VIEW_NAME;
	
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
	
	/**
	 * Sets the URL of a CSS style sheet that should be used to style the 
	 * output. 
	 */
	public void setStyleSheet(String styleSheet) {
		dumpModelView.setStyleSheet(styleSheet);
	}
	
	/**
	 * Sets the viewName that should be resolved to the DumpModelView.
	 * Default is {@link FixedViewNameTranslator#DEFAULT_VIEW_NAME}.
	 */
	public void setDumpViewName(String dumpViewName) {
		this.dumpViewName = dumpViewName;
	}

	public View resolveViewName(String viewName, Locale locale) {
		if (dumpViewName.equals(viewName)) {
			return dumpModelView;
		}
		return null;
	}
}
