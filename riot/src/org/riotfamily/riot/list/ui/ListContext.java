package org.riotfamily.riot.list.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.command.Command;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Context used by ListControllers to render a list.
 */
public class ListContext {

	private ListDefinition listDefinition;
	
	private ListConfig listConfig;
	
	private MutableListParams params;
		
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private MessageResolver messageResolver;

	private Command command;
	
	private String objectId;
	
	private Integer orderBy;
	
	private int rowIndex;
	
	private Integer page;
	

	public ListContext(HttpServletRequest request, 
			HttpServletResponse response, MessageSource messageSource, 
			AdvancedMessageCodesResolver messageKeyResolver, Command command) 
			throws ServletRequestBindingException {
	
		this.request = request;
		this.response = response;
		this.command = command;
		
		this.messageResolver = new MessageResolver(messageSource, 
				messageKeyResolver, RequestContextUtils.getLocale(request));
		
		objectId = request.getParameter(Constants.PARAM_OBJECT_ID);
		
		rowIndex = ServletRequestUtils.getIntParameter(
				request, Constants.PARAM_ROW_INDEX, 0);
		
		orderBy = ServletRequestUtils.getIntParameter(
				request, Constants.PARAM_ORDER_BY);
		
		page = ServletRequestUtils.getIntParameter(
				request, Constants.PARAM_PAGE);
	}

	public ListConfig getListConfig() {
		return listConfig;
	}

	public void setListConfig(ListConfig listConfig) {
		this.listConfig = listConfig;
	}

	public ListDefinition getListDefinition() {
		return listDefinition;
	}

	public void setListDefinition(ListDefinition listDefinition) {
		this.listDefinition = listDefinition;
	}

	public MutableListParams getParams() {
		return params;
	}

	public void setParams(MutableListParams params) {
		this.params = params;
		
		Assert.notNull(listConfig, "listConfig must be set before " +
				"setParams() is called.");
		
		if (page != null) {
			params.setOffset((page.intValue() - 1) * params.getPageSize());
		}
		if (orderBy != null) {
			ColumnConfig col = listConfig.getColumnConfig(orderBy.intValue());
			Assert.isTrue(col.isSortable(), "Column must be sortable");
			params.orderBy(col.getProperty(), col.isAscending(), col.isCaseSensitive());
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}
	
	public MessageResolver getMessageResolver() {
		return messageResolver;
	}

	public Command getCommand() {
		return command;
	}

	public String getObjectId() {
		return objectId;
	}

	public Integer getOrderBy() {
		return orderBy;
	}

	public int getRowIndex() {
		return rowIndex;
	}

}
