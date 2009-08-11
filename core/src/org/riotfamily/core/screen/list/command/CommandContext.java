package org.riotfamily.core.screen.list.command;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.forms.FormContext;


public interface CommandContext {

	public HttpServletRequest getRequest();
	
	public MessageResolver getMessageResolver();
	
	public String getResourcePath();
	
	public String getListKey();
	
	public ListParams getParams();
	
	public int getItemsTotal();
	
	public String getCommandId();
	
	public String getParentId();
	
	public Object getParent();
	
	public ListScreen getScreen();
	
	public ScreenContext createParentContext();
	
	public FormContext createFormContext(String formUrl);
	
	public ScreenContext createNewItemContext(Object parentTreeItem);
	
	public ScreenContext createItemContext(Object item);
	
}
