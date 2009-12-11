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
package org.riotfamily.swarm.jgroups;

import java.io.File;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.swarm.AbstractRiotChannel;
import org.riotfamily.swarm.NodeStatus;
import org.riotfamily.swarm.RiotChannelException;
import org.riotfamily.swarm.RiotChannelListener;
import org.riotfamily.swarm.RiotEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.util.WebUtils;



public class JGroupsChannel extends AbstractRiotChannel
	implements Receiver, InitializingBean, DisposableBean, ServletContextAware {

	private static RiotLog log = RiotLog.get(JGroupsChannel.class);

	private Channel channel;

	private String propertiesFileName;

	private RpcDispatcher rpcDispatcher;

	private ServletContext servletContext;

	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

	public void afterPropertiesSet() throws Exception {
		if (propertiesFileName == null || propertiesFileName.length() == 0) {
			channel = new JChannel();
		} else {
			String filePath = WebUtils.getRealPath(servletContext, propertiesFileName);
			File configFile = new File(filePath);
			if (!configFile.exists()) {
				throw new RiotChannelException("JGroups configuration file not found: " + propertiesFileName);
			}
			channel = new JChannel(configFile);
		}
		channel.setReceiver(this);
		rpcDispatcher = new RpcDispatcher(channel, this, this, this);
		channel.connect(getName());
		if (log.isDebugEnabled()) {
			log.debug("Local address: " + channel.getLocalAddress());
		}

	}

	public Boolean isMasterNode() {
		return Boolean.valueOf(NodeStatus.isMaster());
	}

	public void sendEvent(RiotEvent event) {
		try {
			Message message = new Message(null, null, event);
			channel.send(message);
		}
		catch (ChannelException e) {
			log.error("Sending riot event failed: " + event, e);
		}
	}

	public byte[] getState() {
		return null;
	}

	public void receive(Message msg) {
		RiotEvent event = (RiotEvent) msg.getObject();
		boolean myMessage = msg.getSrc().equals(channel.getLocalAddress());
		if (log.isDebugEnabled()) {
			log.debug("Received riot event (" + (myMessage ? "echo" : "from " + msg.getSrc()) + "): " + event);
		}

		if (!myMessage) {
			Set<RiotChannelListener> listeners = getListeners();
			for (RiotChannelListener listener : listeners) {
				listener.handleEvent(event);
			}
		}
	}

	public void setState(byte[] state) {
		if (log.isDebugEnabled()) {
			log.debug("set state: " + (state != null ? state.length : 0));
		}
	}

	public void block() {
		if (log.isDebugEnabled()) {
			log.debug("block");
		}
	}

	public void suspect(Address adr) {
		if (log.isDebugEnabled()) {
			log.debug("suspect address " + adr);
		}
	}

	public void viewAccepted(View view) {
		if (log.isDebugEnabled()) {
			log.debug("view accepted: " + view.printDetails());
		}
		final Vector<Address> members = view.getMembers();

		Address masterAddress = null;
		if (members.size() == 1) {
			// the only member is master
			NodeStatus.setMaster(true);
			masterAddress = channel.getLocalAddress();
		} else {

			MethodCall call = new MethodCall("isMasterNode",
					null, (Class[])null);
			RspList rspList =  rpcDispatcher.callRemoteMethods(members, call,
					GroupRequest.GET_ALL, 5000);
			Set<Entry<Address, Rsp>> responses = rspList.entrySet();
			for (Entry<Address, Rsp> entry : responses) {
				if (entry.getKey().equals(channel.getLocalAddress())) continue;
				Rsp response = entry.getValue();
				if (!response.wasReceived()) continue;
				if (response.getValue() == Boolean.TRUE) {
					NodeStatus.setMaster(false);
					masterAddress = response.getSender();
					break;
				}
				// have no master in cluster, become a new master
				NodeStatus.setMaster(true);
				masterAddress = channel.getLocalAddress();
			}
		}
		log.debug("Master address: " + masterAddress);
	}

	public void destroy() throws Exception {
		channel.close();
	}
}
