package org.riotfamily.core.status;

import java.util.Map;

import org.riotfamily.core.screen.ModelAndViewScreenlet;
import org.riotfamily.core.screen.ScreenContext;

public abstract class StatusMonitor extends ModelAndViewScreenlet {

	@Override
	protected Class<?> getTemplateClass() {
		return StatusMonitor.class;
	}
	
	@Override
	protected final void populateModel(Map<String, Object> model,
			ScreenContext context) {
		
		model.put("status", getStatus(context));
	}
	
	protected abstract Status getStatus(ScreenContext context);

}
