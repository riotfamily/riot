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
package org.riotfamily.components.model;

import java.util.HashMap;

import org.riotfamily.common.collection.DirtyCheckMap;
import org.springframework.util.Assert;

public class ContentMap extends DirtyCheckMap<String, Object> 
		implements ContentPart {

	private String partId;
	
	private Content owner;

	public ContentMap(Content owner) {
		this(owner, owner.nextPartId());
	}
	
	public ContentMap(Content owner, String partId) {
		super(new HashMap<String, Object>());
		Assert.notNull(owner, "owner must not be null");
		Assert.notNull(partId, "partId must not be null");
		this.owner = owner;
		this.partId = partId;
		owner.registerPart(this);
	}

	/**
	 * Notifies the owner that the content has been modified.
	 */
	@Override
	protected void dirty() {
		owner.dirty();
	}
	
	public String getPartId() {
		return partId;
	}

	public String getId() {
		return owner.getPublicId(this);
	}
	
	public Content getOwner() {
		return owner;
	}
	
	public static ContentMap load(String id) {
		return (ContentMap) Content.loadPart(id);
	}
	
	@Override
	public int hashCode() {
		return partId.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		ContentMap other = (ContentMap) o;
		if (o instanceof ContentMap) {
			return getId().equals(other.getId());
		}
		return false;
	}

}
