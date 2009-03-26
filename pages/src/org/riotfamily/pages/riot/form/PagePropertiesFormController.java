package org.riotfamily.pages.riot.form;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.editor.ContentFormController;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.riot.form.ContentEditorBinder;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.FormContextFactory;
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

	public PagePropertiesFormController(FormContextFactory formContextFactory,
			FormRepository formRepository,
			PlatformTransactionManager transactionManager,
			ComponentDao componentDao, CacheService cacheService) {
		
		super(formContextFactory, formRepository, transactionManager, 
				componentDao, cacheService);
	}

	protected Form createForm(HttpServletRequest request) {
		ContentContainer container = getContainer(request);
		Content content = (Content) getFormBackingObject(request);
		if (container instanceof PageProperties) {
			if (container.getPreviewVersion().equals(content)) {
				return createPagePropertiesForm(request,
						(PageProperties) container);
			}
		}
		return super.createForm(request);
	}
	
	protected Form createPagePropertiesForm(HttpServletRequest request, 
			PageProperties props) {
		
		Page masterPage = props.getPage().getMasterPage();
		
		Form form = new Form(props);
		LocalizedEditorBinder binder = new LocalizedEditorBinder(
				new ContentEditorBinder());
		
		form.setEditorBinder(binder);
		
		String formId = getFormId(request);
		form.setId(formId);
		
		FormFactory factory = getFormRepository().getFormFactory(formId);
		
		for (ElementFactory ef : factory.getChildFactories()) {
			form.addElement(new PagePropertyElement(ef, binder, masterPage));
		}
		return form;
	}
}
