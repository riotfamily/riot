package org.riotfamily.components.editor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.components.cache.ComponentCacheUtils;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.ContentPart;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Controller that displays a form to edit the properties of a ComponentVersion.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ContentFormController extends AbstractFrontOfficeFormController {

	private CacheService cacheService;
	
	public ContentFormController(FormContextFactory formContextFactory,
			FormRepository formRepository,
			PlatformTransactionManager transactionManager,
			CacheService cacheService) {
		
		super(formContextFactory, formRepository, transactionManager);
		this.cacheService = cacheService;
	}

	@SuppressWarnings("unchecked")
	protected void initForm(Form form, HttpServletRequest request) {
		super.initForm(form, request);
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			form.setAttribute(name, request.getParameter(name));
		}
	}
	
	protected Object getFormBackingObject(HttpServletRequest request) {
		return Content.loadPart((String) request.getAttribute("contentId"));
	}
	
	protected Object update(Object object, HttpServletRequest request) {
		ContentPart part = (ContentPart) object;
		ContentContainer container = ContentContainer.loadByContent(part.getOwner());
		ComponentCacheUtils.invalidatePreviewVersion(cacheService, container);
		return part.getOwner().merge();
	}

}
