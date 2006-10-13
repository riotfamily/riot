package org.riotfamily.riot.list.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.riot.dao.Order;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.support.CommandExecutor;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Abstract baseclass for controllers that operate on lists.
 */
public abstract class AbstractListController implements Controller, 
		MessageSourceAware, UrlMappingAware, BeanNameAware {

	protected Log log = LogFactory.getLog(getClass());
	
	private EditorRepository editorRepository;

	private ListRepository listRepository;

	private FormRepository formRepository;
	
	private FormContextFactory formContextFactory;
	
	private MessageSource messageSource;
	
	private PlatformTransactionManager transactionManager;
	
	private CommandExecutor commandExecutor;
	
	private String filterFormTemplate = ResourceUtils.getPath(
			AbstractListController.class, "FilterForm.ftl");

	private String editorIdAttribute = "editorId";

	private String parentIdAttribute = "parentId";

	private String modelKey = "list";

	private String viewName = ResourceUtils.getPath(
			AbstractListController.class, "ListView.ftl");

	private ListParamsManager listParamsManager = new ListParamsManager();

	private UrlMapping urlMapping;
	
	private String beanName;
	
	public AbstractListController(EditorRepository editorRepository, ListRepository listRepository, FormRepository formRepository, PlatformTransactionManager transactionManager, CommandExecutor commandExecutor) {
		this.editorRepository = editorRepository;
		this.listRepository = listRepository;
		this.formRepository = formRepository;
		this.transactionManager = transactionManager;
		this.commandExecutor = commandExecutor;
	}

	public EditorRepository getEditorRepository() {
		return editorRepository;
	}

	public ListRepository getListRepository() {
		return listRepository;
	}

	public FormRepository getFormRepository() {
		return formRepository;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setEditorIdAttribute(String editorIdAttribute) {
		this.editorIdAttribute = editorIdAttribute;
	}

	protected String getEditorIdAttribute() {
		return editorIdAttribute;
	}

	public void setParentIdAttribute(String parentIdAttribute) {
		this.parentIdAttribute = parentIdAttribute;
	}

	protected String getParentIdAttribute() {
		return parentIdAttribute;
	}
	
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	protected String getModelKey() {
		return modelKey;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	protected String getViewName() {
		return viewName;
	}
	
	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public void setFormContextFactory(FormContextFactory formContextFactory) {
		this.formContextFactory = formContextFactory;
	}

	protected PlatformTransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	protected Object execInTransaction(TransactionCallback callback) {
		return new TransactionTemplate(getTransactionManager()).execute(callback);		
	}
	
	public final ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String commandId = request.getParameter(Constants.PARAM_COMMAND);
		
		ListContext context = new ListContext(request, response, 
				messageSource, editorRepository.getMessageCodesResolver(),
				getCommand(commandId));

		ListDefinition listDef = getListDefinition(context);
		if (listDef == null) {
			return null;
		}		
		context.setListDefinition(listDef);
		context.setListConfig(getListConfig(context));
		context.setParams(getListParams(context));
		
		if (commandId != null) {
			return execCommand(context);
		}
		else {
			return createModelAndView(context);
		}
	}

	protected ListDefinition getListDefinition(ListContext context) {
		Object editorId = context.getRequest().getAttribute(editorIdAttribute);
		ListDefinition listDefinition = editorRepository
				.getListDefinition((String) editorId);

		Assert.notNull(listDefinition, "No such editor");
		return listDefinition;
	}

	protected ListConfig getListConfig(ListContext context) {
		ListDefinition listDefinition = context.getListDefinition();
		Assert.notNull(listDefinition, "No ListDefinition in context");

		ListConfig listConfig = listRepository.getListConfig(listDefinition
				.getListId());

		Assert.notNull(listConfig, "No such list in repository: "
				+ listDefinition.getListId());

		return listConfig;
	}

	protected MutableListParams getListParams(ListContext context) {
		ListConfig listConfig = context.getListConfig();
		HttpServletRequest request = context.getRequest();

		MutableListParams params = listParamsManager
				.getListParams(listConfig, request);

		String parentId = (String) request.getAttribute(parentIdAttribute);
		params.setParentId(parentId);

		if (params.getPageSize() == 0) {
			params.setPageSize(listConfig.getPageSize());
		}
		if (!params.hasOrder()) {
			Order order = listConfig.getDefaultOrder();
			if (order != null) {
				params.orderBy(order.getProperty(), order.isAscending(), order.isCaseSensitive());
			}
		}
		return params;
	}

	protected Command getCommand(String commandId) {
		if (commandId == null) {
			return null;
		}
		return getListRepository().getCommand(commandId);
	}

	protected ModelAndView execCommand(final ListContext context) {
		execInTransaction(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus ts) {
				commandExecutor.executeCommand(getListRepository(), context);		
			}
		});
		return null;
	}

	public ModelAndView createModelAndView(final ListContext context) {
		return (ModelAndView) execInTransaction(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus ts) {
				HashMap model = new HashMap();
				String formId = context.getListConfig().getFilterFormId();
				if (formId != null) {
					if (formRepository != null) {
						Form form = formRepository.createForm(formId);
						form.setFormContext(formContextFactory.createFormContext(
									context.getRequest(), context.getResponse()));
						
						form.setTemplate(filterFormTemplate);
						form.setValue(context.getParams().getFilter());
						form.init();
						if ("POST".equals(context.getRequest().getMethod())) {
							form.processRequest(context.getRequest());
						}
						context.getParams().setFilter(form.populateBackingObject());
		
						StringWriter sw = new StringWriter();
						form.render(new PrintWriter(sw));
						model.put("filterForm", sw.toString());
					}
					else {
						log.warn("List is filtered but no FormRepository set!");
					}
				}
		
				ViewModel viewModel = createViewModel(context);
				model.put(getModelKey(), viewModel);
				return new ModelAndView(getViewName(), model);		
			}
		});
	}

	protected String getUrl(Map attrs) {
		return urlMapping.getUrl(beanName, attrs);
	}
	
	protected abstract ViewModel createViewModel(ListContext context);

}
