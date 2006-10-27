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
package org.riotfamily.riot.list.ui;

import java.util.HashMap;

import org.riotfamily.forms.FormRepository;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ui.EditorController;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.list.command.support.CommandExecutor;
import org.riotfamily.riot.security.AccessController;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * Controller that displays lists defined in the ListRepository.
 */
public class ListController extends AbstractListController 
		implements EditorController {
	
	public Class getDefinitionClass() {
		return ListDefinition.class;
	}
	
	public ListController(EditorRepository editorRepository, 
			ListRepository listRepository, FormRepository formRepository,
			PlatformTransactionManager transactionManager,
			CommandExecutor commandExecutor) {
		
		super(editorRepository, listRepository, formRepository, 
				transactionManager, commandExecutor);
	}
	
	public String getUrl(String editorId, String objectId, String parentId) {
		HashMap attrs = new HashMap();
		attrs.put(getEditorIdAttribute(), editorId);
		if (parentId != null) {
			attrs.put(getParentIdAttribute(), parentId);
		}
		return getUrl(attrs);
	}
	
	protected ViewModel createViewModel(final ListContext context) {
		return (ViewModel) execInTransaction(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus ts) {
				ViewModelBuilder viewModelBuilder = new ViewModelBuilder(context);
				return viewModelBuilder.buildModel();
			}
		});
	}
	
	protected ListDefinition getListDefinition(ListContext context) {
		ListDefinition  listDef = super.getListDefinition(context);
		if (!AccessController.isGranted(ACTION_VIEW, null, listDef)) {
			return null;
		}
		else {
			return listDef;
		}
	}

}
