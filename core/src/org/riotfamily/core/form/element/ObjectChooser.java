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
package org.riotfamily.core.form.element;

import java.io.PrintWriter;

import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.ScreenRepository;
import org.riotfamily.forms.element.select.AbstractChooser;

public class ObjectChooser extends AbstractChooser {

	private String rootId;
	
	private String targetId;
	
	private ScreenRepository screenRepository;
	
	private ListScreen rootList;
	
	private ListScreen targetList;

	public ObjectChooser(ScreenRepository screenRepository) {
		this.screenRepository = screenRepository;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	
	@Override
	protected void afterFormSet() {
		targetList = screenRepository.getScreen(targetId, ListScreen.class);
		rootList = screenRepository.getScreen(rootId, ListScreen.class);
	}
	
	@Override
	protected String getChooserUrl() {
		// TODO Auto-generated method stub
		//new ScreenContext(rootList, null, objectId, null, false);
		return "/riot-skeleton/riot/screen/sitemap?choose=sitemap";
	}

	@Override
	protected Object loadBean(String objectId) {
		return targetList.getDao().load(objectId);
	}

	@Override
	protected void renderLabel(Object object, PrintWriter writer) {
		writer.print(targetList.getItemLabel(object));
	}

}
