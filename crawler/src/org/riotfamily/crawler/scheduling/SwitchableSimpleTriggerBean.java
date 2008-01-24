package org.riotfamily.crawler.scheduling;

import java.text.ParseException;
import java.util.Date;

import org.springframework.scheduling.quartz.SimpleTriggerBean;

/**
 * A {@link SimpleTriggerBean} that reads a negative start delay as
 * start never.
 * 
 * @author Alf Werder <alf dot werder at artundweise dot de>
 */
public class SwitchableSimpleTriggerBean extends SimpleTriggerBean  {
	private static final long serialVersionUID = 0L;
	private boolean disabled = false;
	
	public void afterPropertiesSet() throws ParseException {
		super.afterPropertiesSet();
		
		if (disabled) {
			Date epoch = new Date(0); 
			
			setMisfireInstruction(
				MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
			setStartTime(epoch);
			setEndTime(epoch);
			setRepeatCount(0);
		}
	}
	
	public void setStartDelay(long startDelay) {
		super.setStartDelay(startDelay);
		
		disabled = startDelay < 0;
	}
}
