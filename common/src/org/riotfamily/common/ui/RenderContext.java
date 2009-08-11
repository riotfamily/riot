package org.riotfamily.common.ui;

import org.riotfamily.common.i18n.MessageResolver;

public interface RenderContext {

	public MessageResolver getMessageResolver();
	
	public String getContextPath();
	
}
