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
package org.riotfamily.pages.schema;

import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

public class TypeInfo {

	private String name;
	
	private String ref;
	
	private List<TypeInfo> childTypes;

	public TypeInfo() {
	}

	public TypeInfo(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public List<TypeInfo> getChildTypes() {
		return childTypes;
	}

	public void setChildTypes(List<TypeInfo> childTypes) {
		this.childTypes = childTypes;
	}

	void resolve(Map<String, TypeInfo> typeMap) {
		TypeInfo refType = typeMap.get(ref);
		Assert.notNull(refType, "Referenced type not found: " + ref);
		setName(refType.getName());
		setChildTypes(refType.getChildTypes());
	}
	
}
