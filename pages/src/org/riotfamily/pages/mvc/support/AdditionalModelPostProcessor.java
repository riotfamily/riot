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
package org.riotfamily.pages.mvc.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.mvc.ModelBuilder;
import org.riotfamily.pages.mvc.ModelPostProcessor;

/**
 * ModelPostProcessor that adds the result of an additional ModelBuilder to
 * the given model. Note that the second ModelBuilder has no influence on
 * the cache-key or the last-modified date.
 */
public class AdditionalModelPostProcessor implements ModelPostProcessor {
	
	private ModelBuilder modelBuilder;
	
	public void setModelBuilder(ModelBuilder modelBuilder) {
		this.modelBuilder = modelBuilder;
	}
	
	public void postProcess(Map model, HttpServletRequest request)
			throws Exception {

		Map additionalModel = modelBuilder.buildModel(request);
		model.putAll(additionalModel);
	}

}
