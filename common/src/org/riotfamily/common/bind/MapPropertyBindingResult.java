package org.riotfamily.common.bind;

import java.util.Map;

import org.riotfamily.common.beans.property.MapWrapper;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.validation.AbstractPropertyBindingResult;

public class MapPropertyBindingResult extends AbstractPropertyBindingResult {

	protected Map<?, ?> target;
	
    protected ConfigurablePropertyAccessor propertyAccessor;

    public MapPropertyBindingResult(Map<?, ?> target, String objectName) {
        super(objectName);
        this.target = target;
        propertyAccessor = new MapWrapper(target);
    }
    
    public ConfigurablePropertyAccessor getPropertyAccessor() {
        return propertyAccessor;
    }

    public Object getTarget() {
        return target;
    }

}
