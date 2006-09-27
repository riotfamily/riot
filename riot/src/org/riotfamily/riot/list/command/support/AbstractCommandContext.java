package org.riotfamily.riot.list.command.support;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.DisplayDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.ui.ListContext;
import org.riotfamily.riot.list.ui.MutableListParams;

/**
 *
 */
public abstract class AbstractCommandContext implements CommandContext {

	private ListContext listContext;
	
	public AbstractCommandContext(ListContext context) {
		this.listContext = context;
	}
	
	public ListContext getListContext() {
		return listContext;
	}

	public ListDefinition getListDefinition() {
		return listContext.getListDefinition();
	}

	public ListConfig getListConfig() {
		return listContext.getListConfig();
	}
	
	public RiotDao getDao() {
		return getListConfig().getDao();
	}

	public MutableListParams getParams() {
		return listContext.getParams();
	}
	
	public HttpServletRequest getRequest() {
		return listContext.getRequest();
	}
	
	public String getParentId() {
		return getParams().getParentId();
	}

	public DisplayDefinition getEditorDefinition() {
		return getListDefinition().getDisplayDefinition();
	}

	public Class getBeanClass() {
		return getDao().getEntityClass();
	}
	
	public MessageResolver getMessageResolver() {
		return listContext.getMessageResolver();
	}

}
