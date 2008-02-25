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
package org.riotfamily.components.controller.render;

import java.util.List;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.ComponentList;

public class LiveModeRenderStrategy extends CachingRenderStrategy {

	public LiveModeRenderStrategy(ComponentDao dao,
			ComponentRepository repository,	CacheService cacheService) {

		super(dao, repository, cacheService);
	}

	protected boolean isPreview() {
		return false;
	}
	
	/**
	 * Returns the list's live components.
	 */
	protected List getComponentsToRender(ComponentList list) {
		return list.getLiveComponents();
	}

}
