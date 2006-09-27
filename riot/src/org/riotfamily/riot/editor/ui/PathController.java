package org.riotfamily.riot.editor.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Controller that displays a breadcrumb navigation for an editor.
 */
public class PathController implements Controller, MessageSourceAware {

	private EditorRepository repository;
	
	private MessageSource messageSource;
	
	private PlatformTransactionManager transactionManager;
	
	private String editorIdParam = "editorId";

	private String objectIdParam = "objectId";

	private String parentIdParam = "parentId";
	
	private String subPageParam = "subPage";
	
	private String viewName = ResourceUtils.getPath(
			PathController.class, "PathView.ftl");

	private String modelKey = "path";

	public PathController(EditorRepository repository, 
			PlatformTransactionManager transactionManager) {
		
		this.repository = repository;
		this.transactionManager = transactionManager;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getObjectIdParam() {
		return objectIdParam;
	}

	public void setObjectIdParam(String objectIdParam) {
		this.objectIdParam = objectIdParam;
	}

	public String getParentIdParam() {
		return parentIdParam;
	}

	public void setParentIdParam(String parentIdParam) {
		this.parentIdParam = parentIdParam;
	}

	public String getSubPageParam() {
		return this.subPageParam;
	}

	public void setSubPageParam(String subPageParam) {
		this.subPageParam = subPageParam;
	}

	public String getModelKey() {
		return modelKey;
	}

	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public EditorRepository getRepository() {
		return repository;
	}

	/**
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		EditorPath path;
		String editorId = request.getParameter(editorIdParam);
		
		EditorDefinition editor = repository.getEditorDefinition(editorId);
		
		String objectId = request.getParameter(objectIdParam);
		String parentId = request.getParameter(parentIdParam);

		EditorReference lastComponent = createLastPathComponent(
				editor, objectId, parentId, 
				new MessageResolver(messageSource, 
				repository.getMessageCodesResolver(), 
				RequestContextUtils.getLocale(request)));

		path = new EditorPath(editorId, objectId, parentId, 
				lastComponent);

		path.setSubPage(request.getParameter(subPageParam));
		path.encodeUrls(response);
		
		return new ModelAndView(viewName, modelKey, path);
	}

	protected EditorReference createLastPathComponent(
			final EditorDefinition editor, final String objectId, 
			final String parentId, final MessageResolver messageResolver) {
		
		return (EditorReference) new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus ts) {
				return editor.createEditorPath(objectId, parentId, messageResolver);
			}
		});
	}
}
