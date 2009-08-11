package org.riotfamily.components.config;

import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.MapEditorBinder;
import org.riotfamily.forms.factory.DefaultFormFactory;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.xml.XmlFormRepository;
import org.springframework.validation.Validator;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ContentFormRepository extends XmlFormRepository {

	public FormFactory createFormFactory(Class<?> beanClass, 
			FormInitializer initializer, Validator validator) {
		
		if (beanClass == null) {
			DefaultFormFactory factory = new DefaultFormFactory(initializer, validator);
			factory.setEditorBinderClass(MapEditorBinder.class);
			return factory;
		}
		return super.createFormFactory(beanClass, initializer, validator);
	}
	
	public String getContentFormUrl(String formId, Long containerId, Long contentId) {
		if (containsForm(formId)) {
			return "/components/form/" + formId + "/" + containerId + "/" + contentId;
		}
		return null;
	}
	
}
