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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.component.ComponentVersion;

/**
 * Failsafe component that is rendered when an unknown component type is 
 * encountered. This may happen if a component is removed from
 * the ComponentRepository and a reference still exists in the database.  
 */
public class MissingComponent extends AbstractComponent {
	
	private String type;
	
	public MissingComponent(String type) {
		this.type = type;
	}

	protected void renderInternal(ComponentVersion component, 
			String positionClassName, HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		
		PrintWriter out = response.getWriter();
		out.write("No such Component: <code>" + type + "</code>.");
	}
	
}