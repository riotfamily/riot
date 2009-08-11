package org.riotfamily.components.model;

import java.util.ArrayList;

import org.riotfamily.common.collection.DirtyCheckList;
import org.springframework.util.Assert;

public class ComponentList extends DirtyCheckList<Component> 
		implements ContentPart {
	
	private String partId;
	
	private Content owner;

	public ComponentList(Content owner) {
		this(owner, owner.nextPartId());
	}
	
	public ComponentList(Content owner, String partId) {
		super(new ArrayList<Component>());
		Assert.notNull(owner, "owner must not be null");
		Assert.notNull(partId, "partId must not be null");
		this.owner = owner;
		this.partId = partId;
		owner.registerPart(this);
	}

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

	@Override
	public int hashCode() {
		return partId.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		ComponentList other = (ComponentList) o;
		if (o instanceof ComponentList) {
			return getId().equals(other.getId());
		}
		return false;
	}
	
	public static ComponentList load(String listId) {
		return (ComponentList) Content.loadPart(listId);
	}
	
}
