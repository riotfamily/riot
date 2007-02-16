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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.view.freemarker;

import java.util.List;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.common.web.view.ViewContext;
import org.springframework.util.StringUtils;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class OriginalRequestUriMethod implements TemplateMethodModel {
	
	public Object exec(List args) throws TemplateModelException {
		String uri = ServletUtils.getOriginatingRequestUri(ViewContext.getRequest());
		if (StringUtils.hasText(ViewContext.getRequest().getQueryString())) {
			uri = uri + "?" + ViewContext.getRequest().getQueryString();
		}
 		return uri;
	}

}
