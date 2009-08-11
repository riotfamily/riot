package org.riotfamily.core.screen.config;

import org.riotfamily.common.beans.namespace.ChildDecorator;
import org.riotfamily.common.beans.namespace.GenericNamespaceHandlerSupport;
import org.riotfamily.common.beans.namespace.ListDecorator;
import org.riotfamily.common.beans.namespace.ListItemDecorator;
import org.riotfamily.common.beans.namespace.PropertyDecorator;
import org.riotfamily.common.beans.namespace.PropertyValueDecorator;
import org.riotfamily.core.screen.GroupScreen;
import org.riotfamily.core.screen.form.FormScreen;
import org.riotfamily.core.screen.list.ColumnConfig;
import org.riotfamily.core.screen.list.TreeListScreen;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

/**
 * NamespaceHandler that handles the <code>screen</code> namespace as
 * defined in <code>screen.xsd</code> which can be found in the same package.
 */
public class ScreenNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("group", GroupScreen.class).setDecorator(new ChildDecorator()
				.register("screenlets", new ListDecorator())
				.setDefault(new ListItemDecorator("childScreens")));
		
		register("form", FormScreen.class)
				.addTranslation("id", "beanName")
				.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR)
				.setDecorator(new ChildDecorator()
				.register("screenlets", new ListDecorator())
				.setDefault(new ListItemDecorator("childScreens")));
		
		register("list", TreeListScreen.class).setDecorator(new ChildDecorator()
				.register("dao", new PropertyDecorator())
				.register("columns", new ListDecorator())
				.register("commands", new ListDecorator())
				.register("screenlets", new ListDecorator())
				.setDefault(new PropertyValueDecorator("itemScreen")));
		
		register("column", ColumnConfig.class).setDecorator(
				new PropertyDecorator("renderer"));
	}
	
}
