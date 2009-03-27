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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.core.screen.list.dto;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.core.screen.list.command.CommandInfo;

@DataTransferObject
public class CommandButton {

	@RemoteProperty
	private String id;
	
	@RemoteProperty
	private String label;
	
	@RemoteProperty
	private String styleClass;
	
	public CommandButton(String id, CommandInfo info) {
		this.id = id;
		this.label = info.getLabel();
		this.styleClass = info.getStyleClass();
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getStyleClass() {
		return styleClass;
	}
	
}
