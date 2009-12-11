/**
 *
 */
package org.riotfamily.swarm;

import org.riotfamily.common.log.RiotLog;

/**
 * @author alex
 *
 */
public abstract class AbstractRiotChannelAdapter {

	public static RiotLog log = RiotLog.get(CacheChannelAdapter.class);

	private RiotChannel channel;

	public AbstractRiotChannelAdapter() {
		this(null);
	}

	public AbstractRiotChannelAdapter(RiotChannel channel) {
		this.channel = channel;
	}

	public void setChannel(RiotChannel channel) {
		this.channel = channel;
	}

	protected boolean channelExists() {
		return channel != null;
	}

	protected void sendEvent(RiotEvent event) {
		log.debug("Sending event: " + event);
		channel.sendEvent(event);
	}

	public void addListener(RiotChannelListener listener) {
		if (channel != null) {
			channel.addListener(listener);
		}
	}
}
