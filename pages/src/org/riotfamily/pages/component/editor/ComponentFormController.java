package org.riotfamily.pages.component.editor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.beans.propertyeditors.BooleanEditor;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.forms.controller.RepositoryFormController;
import org.riotfamily.forms.element.core.Checkbox;
import org.riotfamily.forms.element.core.FileUpload;
import org.riotfamily.forms.factory.FormDefinitionException;
import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.component.property.FileStoreProperyProcessor;
import org.riotfamily.pages.component.property.PropertyEditorProcessor;
import org.riotfamily.pages.setup.Plumber;
import org.riotfamily.pages.setup.WebsiteConfig;
import org.riotfamily.pages.setup.WebsiteConfigAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

public class ComponentFormController extends RepositoryFormController
		implements FormSubmissionHandler, ApplicationContextAware,
		WebsiteConfigAware, ConfigurationEventListener {

	private static final String SESSION_ATTRIBUTE = "componentForm";
	
	private static final String COMPONENT_ID = "componentId";
	
	private static final String INSTANT_PUBLISH_PARAM = "instantPublish";
	
	private ComponentDao componentDao;
	
	private PlatformTransactionManager transactionManager;
	
	private String viewName = ResourceUtils.getPath(
			ComponentFormController.class, "ComponentFormView.ftl");
	
	private String successViewName = ResourceUtils.getPath(
			ComponentFormController.class, "ComponentFormSuccessView.ftl");
	
	private ComponentRepository componentRepository;
	
	public ComponentFormController(FormRepository formRepository, 
			PlatformTransactionManager transactionManager) {
		
		super(formRepository);
		this.transactionManager = transactionManager;
		
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.form.button.save");
		buttonFactory.setCssClass("button button-save");
		addButton(buttonFactory);
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		Plumber.register(applicationContext, this);
	}
		
	public void setWebsiteConfig(WebsiteConfig websiteConfig) {
		componentRepository = websiteConfig.getComponentRepository();
		componentDao = websiteConfig.getComponentDao();
		componentRepository.addListener(this);
		setupForms(componentRepository.getComponentMap());
	}
	
	public void beanReconfigured(ConfigurableBean bean) {
		setupForms(componentRepository.getComponentMap());
	}
	
	protected void setupForms(Map components) {
		Iterator it = components.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String id = (String) entry.getKey();
			Component component = (Component) entry.getValue();
			try {
				setupForm(component, getFormRepository().createForm(id));
			}
			catch (FormDefinitionException e) {
			}
		}
	}
	
	protected void setupForm(Component component, Form form) {
		componentRepository.addFormId(form.getId());
		Iterator it = form.getRegisteredElements().iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			if (e instanceof FileUpload) {
				FileUpload upload = (FileUpload) e;
				component.addPropertyProcessor(
						new FileStoreProperyProcessor(
						upload.getEditorBinding().getProperty(),
						upload.getFileStore()));
			}
			else if (e instanceof Checkbox) {
				Checkbox cb = (Checkbox) e;
				component.addPropertyProcessor(
						new PropertyEditorProcessor(
						cb.getEditorBinding().getProperty(),
						new BooleanEditor()));
			}
		}
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setSuccessViewName(String successViewName) {
		this.successViewName = successViewName;
	}
	
	protected ComponentVersion getPreview(HttpServletRequest request) {
		Long id = new Long((String) request.getAttribute(COMPONENT_ID));
		VersionContainer container = componentDao.loadVersionContainer(id);
		boolean instanPublishMode = ServletRequestUtils.getBooleanParameter(
				request, INSTANT_PUBLISH_PARAM, false);
		
		if (instanPublishMode) {
			return container.getLiveVersion();
		}
		
		ComponentVersion preview = container.getPreviewVersion();
		if (preview == null) {
			ComponentVersion live = container.getLiveVersion();
			Component component = componentRepository.getComponent(live.getType());
			preview = component.copy(live);
			container.setPreviewVersion(preview);
			componentDao.updateVersionContainer(container);
		}
		return preview;
	}
	
	public String getFormId(String componentType) {
		return componentRepository.getFormId(componentType);
	}
	
	protected String getFormId(HttpServletRequest request) {
		ComponentVersion preview = getPreview(request);
		return getFormId(preview.getType());
	}
		
	protected Object getFormBackingObject(HttpServletRequest request) {
		ComponentVersion preview = getPreview(request);
		Component component = componentRepository.getComponent(preview);
		return component.buildModel(preview);
	}
	
	protected String getSessionAttribute(HttpServletRequest request) {
		return SESSION_ATTRIBUTE;
	}
	
	protected ModelAndView showForm(final Form form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return (ModelAndView) execInTransaction(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				StringWriter sw = new StringWriter();
				renderForm(form, new PrintWriter(sw));
				return new ModelAndView(viewName, "form", sw.toString());
			}
		});		
	}
	
	public ModelAndView handleFormSubmission(Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	
		ComponentVersion preview = getPreview(request);
		Component component = componentRepository.getComponent(preview);
		Map properties = (Map) form.populateBackingObject();
		component.updateProperties(preview, properties);
		componentDao.updateComponentVersion(preview);
		return new ModelAndView(successViewName);
	}
	
	protected void processAjaxRequest(final Form form, final HttpServletRequest request, 
					final HttpServletResponse response) throws IOException {
		
		execInTransaction(new TransactionCallbackWithoutResult() {
		
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				try {
					ComponentFormController.super.processAjaxRequest(form, request, response);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}		
		});		
	}
	
	protected Object execInTransaction(TransactionCallback callback) {
		return new TransactionTemplate(transactionManager).execute(callback);		
	}

}
