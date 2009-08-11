package org.riotfamily.forms.element;

import java.util.Collection;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.AbstractElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.StylesheetResource;

public abstract class AbstractResourceElement extends AbstractElement 
		implements ResourceElement {

	private Collection<FormResource> resources = Generics.newLinkedList();
	
	protected void addScriptResource(String src) {
		addResource(new ScriptResource(src));
	}
	
	protected void addStylesheetResource(String src) {
		addResource(new StylesheetResource(src));
	}
	
	protected void addResource(FormResource resource) {
		resources.add(resource);
	}
	
	public Collection<FormResource> getResources() {
		return resources;
	}
}
