package org.riotfamily.common.servlet;

/**
 * Bean that is looked up by the {@link ReloadableDispatcherServlet} to 
 * determine whether reload checks should be enabled. If no instance of this 
 * class is found, the DispatcherServlet will use the the value obtained from
 * the <code>reloadable</code> init-parameter.
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ReloadableDispatcherServletConfig {

	private boolean reloadable;

	public boolean isReloadable() {
		return this.reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}
	
}
