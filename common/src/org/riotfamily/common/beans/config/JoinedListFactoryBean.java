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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.beans.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.config.AbstractFactoryBean;

public class JoinedListFactoryBean extends AbstractFactoryBean {

	private List<Collection<?>> lists;
	
	public void setLists(List<Collection<?>> lists) {
		this.lists = lists;
	}
	
	@Override
	protected Object createInstance() throws Exception {
		ArrayList<Object> joinedList = new ArrayList<Object>();
		for (Collection<?> list : lists) {
			if (list != null) {
				joinedList.addAll(list);
			}
		}
		return joinedList;
	}

	@Override
	public Class<?> getObjectType() {
		return ArrayList.class;
	}

}
