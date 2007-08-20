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
package org.riotfamily.common.web.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.filter.ResourceStamper;
import org.riotfamily.common.web.mapping.ReverseHandlerMapping;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CommonMacroHelperFactory implements MacroHelperFactory, 
		ApplicationContextAware {

	private ResourceStamper stamper;
	
	private List mappings;
	
	public void setStamper(ResourceStamper stamper) {
		this.stamper = stamper;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) 
			throws BeansException {
		
		mappings = new ArrayList(applicationContext.getBeansOfType(
				ReverseHandlerMapping.class).values());
		
		if (!mappings.isEmpty()) {
			Collections.sort(mappings, new OrderComparator());
		}
	}

	public Object createMacroHelper(HttpServletRequest request, 
			HttpServletResponse response) {
		
		return new CommonMacroHelper(request, response, stamper, mappings);
	}

}
