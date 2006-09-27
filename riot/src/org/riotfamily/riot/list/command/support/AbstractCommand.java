package org.riotfamily.riot.list.command.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.ui.render.RenderContext;
import org.springframework.beans.factory.BeanNameAware;

/**
 * Abstract baseclass for commands.
 */
public abstract class AbstractCommand implements Command, BeanNameAware {

	private static final String COMMAND_NAME_SUFFIX = "Command";

	protected Log log = LogFactory.getLog(getClass());
	
	private String id;
	
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the commandId. If no value is set the bean name will be used 
	 * by default.
	 * 
	 * @see #setBeanName(String)
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Implementation of the 
	 * {@link org.springframework.beans.factory.BeanNameAware BeanNameAware}
	 * interface. If no command id is explicitly set, the bean name will be
	 * used instead. Note that if the name ends with the suffix "Command" 
	 * it will be removed from the id.  
	 */
	public void setBeanName(String beanName) {
		if (id == null) {
			if (beanName.endsWith(COMMAND_NAME_SUFFIX)) {
				beanName = beanName.substring(0, beanName.length() - 
						COMMAND_NAME_SUFFIX.length());
			}
			id = beanName;
		}
	}
	
	/**
	 * Always returns <code>null</code>. Sublasses may override this method
	 * in order to display a confirmation message before the command is
	 * executed.
	 */
	public String getConfirmationMessage(CommandContext context) {
		return null;
	}

	/**
	 * Returns the command's id. Subclasses may override this method if the
	 * action depends on the context.
	 */
	public String getAction(CommandContext context) {
		return getId();
	}

	/**
	 * Always returns <code>true</code>. Subclasses may override this method
	 * to disable the command depending on the context.
	 */
	public boolean isEnabled(RenderContext context) {
		return true;
	}

}
