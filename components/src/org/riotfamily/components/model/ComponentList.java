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

import java.util.Date;
import java.util.List;


/**
 * List of components that can be looked up using a {@link Location}.
 * Actually the class consists of two lists of
 * {@link org.riotfamily.components.model.VersionContainer VersionContainers},
 * the live-list and the preview-list.
 */
public class ComponentList {

	private Long id;

	private Location location;

	private List liveContainers;

	private List previewContainers;

	private boolean dirty;

	private Date lastModified;

	private String lastModifiedBy;

	private VersionContainer parent;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List getLiveContainers() {
		return this.liveContainers;
	}

	public void setLiveContainers(List list) {
		this.liveContainers = list;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public VersionContainer getParent() {
		return this.parent;
	}

	public void setParent(VersionContainer parent) {
		this.parent = parent;
	}

	public List getPreviewContainers() {
		return this.previewContainers;
	}

	public void setPreviewContainers(List list) {
		this.previewContainers = list;
	}

	/**
	 * Returns whether the list has a preview-list. The flag is needed because
	 * Hibernate does not distinguish between a null collection reference and
	 * an empty collection.
	 */
	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Returns the date of the last modification.
	 * @since 6.4
	 */
	public Date getLastModified() {
		return this.lastModified;
	}

	/**
	 * Sets the date of the last modification.
	 * @since 6.4
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * Returns the principal that made the last modification.
	 * @since 6.4
	 */
	public String getLastModifiedBy() {
		return this.lastModifiedBy;
	}

	/**
	 * Sets the principal that made the last modification.
	 * @since 6.4
	 */
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ComponentList ");
		sb.append(location);
		sb.append(" (").append(id).append(')');
		return sb.toString();
	}

}
