/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
