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
package org.riotfamily.components.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ContentList extends Content {

	private List contentList;

	public ContentList() {
	}

	public ContentList(List contentList) {
		this.contentList = contentList;
	}

	public Object getValue() {
		return contentList;
	}
	
	public void setValue(Object value) {
		contentList = (List) value;
	}

	public Object unwrap() {
		if (contentList == null) {
			return null;
		}
		ArrayList result = new ArrayList(contentList.size());
		Iterator it = contentList.iterator();
		while (it.hasNext()) {
			Content content = (Content) it.next();
			if (content != null) {
				result.add(content.unwrap());
			}
			else {
				result.add(null);
			}
		}
		return Collections.unmodifiableList(result);
	}
	
	public Content deepCopy() {
		ArrayList copy = new ArrayList(contentList.size());
		Iterator it = contentList.iterator();
		while (it.hasNext()) {
			Content content = (Content) it.next();
			if (content != null) {
				copy.add(content.deepCopy());
			}
			else {
				copy.add(null);
			}
		}
		return new ContentList(copy);
	}
	
	public Collection getCacheTags() {
		if (contentList == null) {
			return null;
		}
		HashSet result = new HashSet();
		Iterator it = contentList.iterator();
		while (it.hasNext()) {
			Content content = (Content) it.next();
			if (content != null) {
				Collection tags = content.getCacheTags();
				if (tags != null) {
					result.addAll(tags);
				}
			}
		}
		return result;
	}

	public List getContents() {
		return contentList;
	}

	public void clear() {
		if (contentList != null) {
			contentList.clear();
		}
	}

	public void addContent(Content content) {
		if (contentList == null) {
			contentList = new ArrayList();
		}
		contentList.add(content);
	}
	
	
}
