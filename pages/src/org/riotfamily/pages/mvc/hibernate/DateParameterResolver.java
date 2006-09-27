package org.riotfamily.pages.mvc.hibernate;

import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;


public class DateParameterResolver extends AbstractParameterResolver {

	private String yearparam;

	private String monthparam;

	private String dayparam;

	public String getDayparam() {
		return dayparam;
	}

	public void setDayparam(String dayparam) {
		this.dayparam = dayparam;
	}

	public String getMonthparam() {
		return monthparam;
	}

	public void setMonthparam(String monthparam) {
		this.monthparam = monthparam;
	}

	public String getYearparam() {
		return yearparam;
	}

	public void setYearparam(String yearparam) {
		this.yearparam = yearparam;
	}

	public Object getValueInternal(HttpServletRequest request) {
		Integer year = getIntegerParameter(request, yearparam); 
		Integer month = getIntegerParameter(request, monthparam); 
		Integer day = getIntegerParameter(request, dayparam);
		
		if (year != null) {
			if (month != null && month.intValue() >=0 && month.intValue() <=11) {
				if (day != null && day.intValue() >=1 && day.intValue() <= 31) {
					return new GregorianCalendar(year.intValue(), month.intValue(), day.intValue()).getTime();
				}
				return new GregorianCalendar(year.intValue(), month.intValue(), 1).getTime();
			}
		}
		
		return null;
	}
	
	private Integer getIntegerParameter(HttpServletRequest request, String param) {
		try {
			return Integer.valueOf(request.getParameter(param));
		} catch (Exception e) {
			
		}
		return null;
	} 

}
