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

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class providing common functionality for clustering.
 *
 * @author Lars Behnke
 * @since 6.5
 */
public abstract class AbstractRiotChannel implements RiotChannel {

	private String name;

	private Set<RiotChannelListener> listeners = new HashSet<RiotChannelListener>();

	public String getName() {
		return this.name;
	}

	public void setName(String channelName) {
		this.name = channelName;
	}

	public void addListener (RiotChannelListener listener) {
		listeners.add(listener);
	}

	protected Set<RiotChannelListener> getListeners() {
		return listeners;
	}

}
