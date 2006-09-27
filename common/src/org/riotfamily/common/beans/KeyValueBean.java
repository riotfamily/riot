package org.riotfamily.common.beans;

public class KeyValueBean {

	private Object key;
	
	private Object value;

	
	public KeyValueBean() {
	}
	
	public KeyValueBean(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return this.key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
