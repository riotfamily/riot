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
			tag.body(getLabel(context));
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
		
		String key = property;
        int pos = property.indexOf('.');
        if (pos > 0) {
            property = property.substring(0, pos);
        }
        if (pos > 0 && lookupLevel > 1) {
            clazz = PropertyUtils.getPropertyType(clazz, property);
            property = key.substring(pos + 1);
            return getLabel(context, clazz, property, lookupLevel - 1);
        }
        clazz = PropertyUtils.getDeclaringClass(clazz, property);
        return context.getMessageResolver().getPropertyLabel(
        		context.getListConfig().getId(), clazz, property);
	}
}
