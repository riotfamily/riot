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
package org.riotfamily.pages.riot;

import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.FormDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class EditorRepositoryInitializer implements ConfigurationEventListener {

	private EditorRepository repository;

	public EditorRepositoryInitializer(EditorRepository repository) {
		this.repository = repository;
		repository.addListener(this);
		initRepository();
	}
	
	public void beanReconfigured(ConfigurableBean bean) {
		initRepository();
	}
	
	protected void initRepository() {
		ListDefinition pages = repository.getListDefinition("pages");
		
		ListConfig listConfig = pages.getListConfig();
		ColumnConfig localeColumn = listConfig.getColumnConfig("locale");
		listConfig.getColumnConfigs().remove(localeColumn);
		
		pages.setId("sitemap");
		repository.addEditorDefinition(pages);
		repository.getRootGroupDefinition().addEditorDefinition(pages);
		
		FormDefinition pageForm = (FormDefinition) pages.getDisplayDefinition();
		//pageForm.addChildEditorDefinition(editorDef);
	}
	
}
