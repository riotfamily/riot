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


/**
 * Contract for classes that provide for clustering capability.
 *
 * @author Lars Behnke
 * @since 6.5
 */
public interface RiotChannel {

	/**
	 * Send an arbitrary event to all members of a channel.
	 * @param event The event wrapping the message.
	 */
	void sendEvent (RiotEvent event);

	/**
	 * Adds a listener to the list of message recipients.
	 * @param listener The listener to add.
	 */
	void addListener (RiotChannelListener listener);
}
