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
 *   mgaudig
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.swarm;

import java.io.Serializable;

/**
 * Event exchanged between channel participants.
 *
 * @author Lars Behnke
 * @since 6.5
 */
public class RiotEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private String messageType;

	private Serializable[] params;

	public RiotEvent() {
		this(null);
	}

	public RiotEvent(String messageType) {
		super();
		this.messageType = messageType;
	}

	public String getMessageType() {
		return this.messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public Serializable[] getParams() {
		return this.params;
	}

	public void setParams(Serializable[] params) {
		this.params = params;
	}

	public void setParam(Serializable param) {
		this.params = new Serializable[]{param};
	}

	public Serializable getParam() {
		if (params == null || params.length < 1) {
			return null;
		} else {
			return params[0];
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("Event ");
		sb.append(messageType);
		if (params != null) {
			sb.append(" - ");
			for (int i = 0; i < params.length; i++) {
				if (i > 0) sb.append(", ");
				sb.append(params[i]);
			}
		}
		return sb.toString();
	}
}
