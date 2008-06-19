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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.view.freemarker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.core.OrderComparator;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PluginObjectWrapper extends DefaultObjectWrapper {

	private ArrayList<ObjectWrapperPlugin> plugins;
	
	public PluginObjectWrapper(Collection<ObjectWrapperPlugin> plugins) {
		this.plugins = new ArrayList<ObjectWrapperPlugin>(plugins);
		Collections.sort(this.plugins, new OrderComparator());
	}
	
	public TemplateModel wrap(Object obj) throws TemplateModelException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof TemplateModel) {
			return (TemplateModel) obj;
		}
		for (ObjectWrapperPlugin plugin : plugins) {
			if (plugin.supports(obj)) {
				return plugin.wrapSupportedObject(obj, this);
			}
		}
		return wrapUnsupportedObject(obj);
	}
	
	public TemplateModel wrapUnsupportedObject(Object obj) 
			throws TemplateModelException {
		
		return super.wrap(obj);
	}
	
}
