package org.riotfamily.statistics.domain;


public class NamedEntity {
	
	private String name;

	private int idx;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public NamedEntity() {
		this (null);
	}
	
	public NamedEntity(String name) {
		super();
		this.name = name;
	}

	


}
