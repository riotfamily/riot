package org.riotfamily.components.config;

import java.util.Map;

import org.riotfamily.common.beans.property.PropertyUtils;

public class ComponentConfig {

	private String type;
	
	private int min;
	
	private Integer max;
	
	
	@SuppressWarnings("unchecked")
	public ComponentConfig(Object obj) {
		if (obj instanceof String) {
			type = (String) obj;
		}
		else if (obj instanceof Map) {
			Map map = (Map) obj;
			PropertyUtils.setProperties(this, map);
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

}
