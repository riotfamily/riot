package org.riotfamily.common.bind;

import java.util.Map;

import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * {@link ServletRequestDataBinder} that binds request parameters to a Map
 * instead of a bean. 
 */
public class MapServletRequestDataBinder extends ServletRequestDataBinder {

	private MapPropertyBindingResult bindingResult;
	
	public MapServletRequestDataBinder(Object target, String objectName) {
		super(target, objectName);
	}

	public MapServletRequestDataBinder(Object target) {
		super(target);
	}

	@Override
	protected AbstractPropertyBindingResult getInternalBindingResult() {
		if (bindingResult == null) {
			bindingResult = new MapPropertyBindingResult((Map<?, ?>) getTarget(), getObjectName());
		}
		return bindingResult;
	}
	
}
