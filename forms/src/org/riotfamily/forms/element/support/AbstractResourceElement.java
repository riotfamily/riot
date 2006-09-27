package org.riotfamily.forms.element.support;

import java.util.Collection;
import java.util.LinkedList;

import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.StylesheetResource;

public abstract class AbstractResourceElement extends AbstractElement 
		implements ResourceElement {

	private Collection resources = new LinkedList();
	
	protected void addScriptResource(String src) {
		addResource(new ScriptResource(src));
	}
	
	protected void addStylesheetResource(String src) {
		addResource(new StylesheetResource(src));
	}
	
	protected void addResource(FormResource resource) {
		resources.add(resource);
	}
	
	public Collection getResources() {
		return resources;
	}
}
