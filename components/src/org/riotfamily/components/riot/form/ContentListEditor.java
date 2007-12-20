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
package org.riotfamily.components.riot.form;

import java.util.Iterator;

import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentList;
import org.riotfamily.components.service.ContentFactory;
import org.riotfamily.forms.element.collection.ListEditor;
import org.riotfamily.forms.element.collection.ListItem;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ContentListEditor extends ListEditor implements ContentEditor {

	private ContentFactory contentFactory;
	
	private ContentList contentList;
	
	public ContentListEditor(ContentFactory contentFactory) {
		this.contentFactory = contentFactory;
	}

	public void setValue(Object value) {
		if (value != null) {
			if (value instanceof ContentList) {
				contentList = (ContentList) value;
				Iterator it = contentList.getContents().iterator();			
				while (it.hasNext()) {
					ListItem item = addItem();
					item.setValue(it.next());
				}	
			}
			else {
				throw new IllegalArgumentException("Value must be a ContentList");
			}
		}
	}
	
	public Object getValue() {
		if (contentList == null) {
			contentList = new ContentList();
		}
		else {
			contentList.clear();
		}
		Iterator it = getListItems().iterator();
		while (it.hasNext()) {
			ListItem item = (ListItem) it.next();
			Object value = item.getValue();
			if (value != null) {
				contentList.addContent((Content) value);
			}
		}
		return contentList;
	}
	
	protected ListItem createItem() {
		return new ContentListItem(this, contentFactory);
	}
	
}
