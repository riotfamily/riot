package org.riotfamily.riot.list.command;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.DisplayDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.ui.MutableListParams;

/**
 * Context passed to commands during command execution.
 */
public interface CommandContext {

	public ListDefinition getListDefinition();
	
	public ListConfig getListConfig();
	
	public RiotDao getDao();
	
	public MutableListParams getParams();
	
	public int getRowIndex();

	public DisplayDefinition getEditorDefinition();

	public Class getBeanClass();

	public String getParentId();
	
	public String getObjectId();

	public Object getItem();

	public boolean isConfirmed();
		
	public MessageResolver getMessageResolver();
	
	public HttpServletRequest getRequest();
	
}