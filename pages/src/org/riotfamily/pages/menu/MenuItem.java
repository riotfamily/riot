package org.riotfamily.pages.menu;

import java.util.List;

public class MenuItem {

	private String label;
	
	private String title;
	
	private String link;
	
	private String target;
	
	private boolean expanded;
	
	private boolean active;
	
	private String style;
	
	private List childItems;
	
	public MenuItem() {
	}
	
	public MenuItem(String label, String link) {
		this.label = label;
		this.link = link;
	}

	public MenuItem(String label, String link, String style) {
		this.label = label;
		this.link = link;
		this.style = style;
	}
	
	public MenuItem(String label, String link, String style, String title) {
		this.label = label;
		this.link = link;
		this.style = style;
		this.title = title;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List getChildItems() {
		return this.childItems;
	}

	public void setChildItems(List childItems) {
		this.childItems = childItems;
	}

	public boolean isExpanded() {
		return this.expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
	public String toString() {
		return label;
	}
	
}
