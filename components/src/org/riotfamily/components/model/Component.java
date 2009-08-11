package org.riotfamily.components.model;

import java.util.ListIterator;

import org.springframework.util.Assert;


public class Component extends ContentMap {

	private ComponentList list;
	
	private String type;
	
	public Component(ComponentList list) {
		super(list.getOwner());
		this.list = list;
	}
	
	public Component(ComponentList list, String id) {
		super(list.getOwner(), id);
		this.list = list;
	}

	public ComponentList getList() {
		return list;
	}

	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public void delete() {
		Assert.isTrue(list.remove(this));
	}
	
	public void move(String before) {
		delete();
		if (before != null) {
			ListIterator<Component> it = list.listIterator();
			while (it.hasNext()) {
				if (it.next().getId().equals(before)) {
					it.add(this);
					break;
				}
			}
		}
		else {
			list.add(this);
		}
	}
	
	public static Component load(String id) {
		return (Component) Content.loadPart(id);
	}

	public int getPosition() {
		return list.indexOf(this);
	}

}
