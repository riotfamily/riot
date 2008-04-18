package org.riotfamily.pages.riot.form;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.editor.ContentFormController;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.riot.form.ContentContainerEditorBinder;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageProperties;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * The pages module replaces the ContentFormController provided by the
 * components module by this implementation. It checks if the  
 * {@link ContentContainer} being edited is a {@link PageProperties} instance
 * and wraps the form elements with {@link PagePropertyElement}s.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePropertiesFormController extends ContentFormController {

	public PagePropertiesFormController(FormRepository formRepository,
			PlatformTransactionManager transactionManager,
			ComponentDao componentDao) {
		
		super(formRepository, transactionManager, componentDao);
	}

	protected Form createForm(HttpServletRequest request) {
		Object backingObject = getFormBackingObject(request);
		if (backingObject instanceof PageProperties) {
			return createPagePropertiesForm(request,
					(PageProperties) backingObject);
		}
		return super.createForm(request);
	}
	
	protected Form createPagePropertiesForm(HttpServletRequest request, 
			PageProperties props) {
		
		Page masterPage = props.getPage().getMasterPage();
		
		Form form = new Form(props);
		LocalizedEditorBinder binder = new LocalizedEditorBinder(
				new ContentContainerEditorBinder());
		
		form.setEditorBinder(binder);
		
		String formId = getFormId(request);
		FormFactory factory = getFormRepository().getFormFactory(formId);
		
		Iterator it = factory.getChildFactories().iterator();
		while (it.hasNext()) {
			ElementFactory ef = (ElementFactory) it.next();
			form.addElement(new PagePropertyElement(ef, binder, masterPage));
		}
		return form;
	}
}
