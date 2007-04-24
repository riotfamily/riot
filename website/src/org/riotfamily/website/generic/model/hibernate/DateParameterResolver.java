/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.generic.model.hibernate;

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
		} 
		catch (NumberFormatException e) {
		}
		return null;
	} 

}
