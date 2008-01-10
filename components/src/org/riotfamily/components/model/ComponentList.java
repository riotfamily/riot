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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * List of components that can be looked up using a {@link ComponentListLocation}.
 * The class consists of two lists of
 * {@link org.riotfamily.components.model.Component components},
 * the liveComponents and the previewComponents.
 */
public class ComponentList {

	private Long id;

	private ComponentListLocation location;

	private List liveComponents;

	private List previewComponents;

	private boolean dirty;

	private Date lastModified;

	private String lastModifiedBy;

	private Component parent;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List getLiveComponents() {
		return this.liveComponents;
	}

	public void setLiveComponents(List list) {
		this.liveComponents = list;
	}

	public ComponentListLocation getLocation() {
		return this.location;
	}

	public void setLocation(ComponentListLocation location) {
		this.location = location;
	}

	public Component getParent() {
		return this.parent;
	}

	public void setParent(Component parent) {
		this.parent = parent;
	}

	public List getPreviewComponents() {
		return this.previewComponents;
	}

	public void setPreviewComponents(List list) {
		this.previewComponents = list;
	}
	
	public List getOrCreatePreviewContainers() {
		if (!dirty) {
			if (previewComponents == null) {
				previewComponents = new ArrayList();
			}
			else {
				previewComponents.clear();
			}
			if (liveComponents != null) {
				previewComponents.addAll(liveComponents);
			}
			dirty = true;
		}
		return previewComponents;
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
	
	public void insertContainer(Component container, int position) {
		List containers = getOrCreatePreviewContainers();
		container.setList(this);
		if (position >= 0) {
			containers.add(position, container);
		}
		else {
			containers.add(container);
		}
		setDirty(true);
	}
	
	public ComponentList createCopy(String path) {
		ComponentList copy = new ComponentList();
		ComponentListLocation location = new ComponentListLocation(this.location);
		location.setPath(path);
		copy.setLocation(location);
		copy.setDirty(dirty);
		copy.setLiveComponents(copyContainers(liveComponents, path));
		copy.setPreviewComponents(copyContainers(previewComponents, path));
		return copy;
	}
	
	private List copyContainers(List source, String path) {
		if (source == null) {
			return null;
		}
		List dest = new ArrayList(source.size());
		Iterator it = source.iterator();
		while (it.hasNext()) {
			Component container = (Component) it.next();
			dest.add(container.createCopy(path));
		}
		return dest;
	}	

}
