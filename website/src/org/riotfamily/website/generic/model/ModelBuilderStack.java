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
 *   alf
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.generic.model;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.Generics;

/**
 * @author Alf Werder <alf dot werder at artundweise dot de>
 * @since 6.5
 */
public class ModelBuilderStack implements CacheableModelBuilder {
	private ModelBuilder[] modelBuilders = new ModelBuilder[] {};
	
	public boolean isCacheable() {
		for (int i=0; i<modelBuilders.length; i++) {
			if (!(modelBuilders[i] instanceof CacheableModelBuilder)) {
				return false;
			}
		}
		
		return true;
	}
	
	public void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		if (isCacheable()) {
			for (int i=0; i<modelBuilders.length; i++) {
				((CacheableModelBuilder) modelBuilders[i]).appendCacheKey(
					key, request);
			}
		}
	}
	
	public long getLastModified(HttpServletRequest request) {
		if (isCacheable()) {
			long lastModified = 0;
			
			for (int i=0; i<modelBuilders.length; i++) {
				CacheableModelBuilder modelBuilder =
					(CacheableModelBuilder) modelBuilders[i];
				
				lastModified = Math.max(lastModified,
					modelBuilder.getLastModified(request));
			}
			
			return lastModified;
		} else {
			return new Date().getTime();
		}
	}

	public long getTimeToLive() {
		if (isCacheable()) {
			long timeToLive = Long.MAX_VALUE;
			
			for (int i=0; i<modelBuilders.length; i++) {
				CacheableModelBuilder modelBuilder =
					(CacheableModelBuilder) modelBuilders[i];
				
				timeToLive = Math.min(timeToLive, modelBuilder.getTimeToLive());
			}
			
			return timeToLive;
		} else {
			return 0;
		}
	}

	public Map<String, Object> buildModel(HttpServletRequest request) throws Exception {
		Map<String, Object> model = Generics.newHashMap();
		
		for (int i=0; i<modelBuilders.length; i++) {
			model.putAll(modelBuilders[i].buildModel(request));
		}
		
		return model;
	}

	public void setModelBuilders(ModelBuilder[] modelBuilders) {
		this.modelBuilders = modelBuilders;
	}
}
