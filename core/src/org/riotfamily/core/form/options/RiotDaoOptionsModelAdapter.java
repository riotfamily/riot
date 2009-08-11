package org.riotfamily.core.form.options;

import java.util.ArrayList;
import java.util.Collection;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.list.ListParamsImpl;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.OptionsModelAdapter;
import org.riotfamily.forms.options.OptionsModel;
import org.riotfamily.forms.options.StaticOptionsModel;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RiotDaoOptionsModelAdapter implements OptionsModelAdapter {

	public boolean supports(Object model) {
		return model instanceof RiotDao;
	}
	
	public OptionsModel adapt(Object model, Element element) {
		RiotDao dao = (RiotDao) model;
		Collection<?> values = dao.list(null, new ListParamsImpl());
		Form form = element.getForm();
		//Remove the object being edited from the collection to prevent circular 
		//references. This is only done when editing an existing object and 
		//classes are assignment compatible.
		if (!form.isNew() && dao.getEntityClass().isAssignableFrom(
				form.getEditorBinder().getBeanClass())) {
			
			ArrayList<?> copy = Generics.newArrayList(values);
			copy.remove(form.getBackingObject());
			return new StaticOptionsModel(copy);
		}
		return new StaticOptionsModel(values);
	}
}
