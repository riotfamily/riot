package org.riotfamily.forms.resource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public interface ResourceVisitor {

	public void visitStyleSheet(StylesheetResource res);
	
	public void visitScript(ScriptResource res);

}
