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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;
import java.util.List;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.riot.dao.Order;


/**
 * CellRenderer that renders column headings.
 */
public class HeadingRenderer implements CellRenderer {

	public void render(RenderContext context, PrintWriter writer) {
		String property = context.getProperty();
		if (property != null) {
			TagWriter tag = new TagWriter(writer);
			tag.start(Html.SPAN);
			Order primaryOrder = null;
			List order = context.getParams().getOrder();
			if (order != null && !order.isEmpty()) {
				primaryOrder = (Order) order.get(0);
			}
			if (primaryOrder != null) {
				if (property.equals(primaryOrder.getProperty())) {
					tag.attribute(Html.COMMON_CLASS, "sorted-" 
							+ (primaryOrder.isAscending() ? "asc" : "desc"));
				}
			}
			tag.body(getLabel(context), false);
			tag.end();
		}
	}
	
	protected String getLabel(RenderContext context) {
		return getLabel(context, 
				context.getBeanClass(), 
				context.getProperty(), 
				context.getColumnConfig().getLookupLevel());
	}
	
	protected String getLabel(RenderContext context, Class clazz, 
			String property, int lookupLevel) {

		String root = property;
        int pos = property.indexOf('.');
        if (pos > 0) {
            root = property.substring(0, pos);
        }
        if (lookupLevel > 1) {
        	clazz = PropertyUtils.getPropertyType(clazz, root);
        	String nestedProperty = property.substring(pos + 1);
        	return getLabel(context, clazz, nestedProperty, lookupLevel - 1);
        }
	    return context.getMessageResolver().getPropertyLabel(
	    		context.getListConfig().getId(), clazz, property);
	}
}
