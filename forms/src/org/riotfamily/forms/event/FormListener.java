package org.riotfamily.forms.event;

import org.riotfamily.forms.Element;

/**
 *
 */
public interface FormListener {

	public void elementRendered(Element element);
	
	public void elementChanged(Element element);
	
	public void elementRemoved(Element element);
	
	public void elementAdded(Element element);
	
	public void elementFocused(Element element);
	
	public void elementEnabled(Element element);	
	
	public void refresh(Element element);
}
