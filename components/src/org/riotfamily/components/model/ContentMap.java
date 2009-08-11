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
