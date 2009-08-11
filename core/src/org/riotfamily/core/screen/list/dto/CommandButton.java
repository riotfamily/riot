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
	private String icon;
	
	@RemoteProperty
	private boolean enabled;
	
	public CommandButton(String id, CommandInfo info) {
		this(id, info, false);
	}
	
	public CommandButton(String id, CommandInfo info, boolean enabled) {
		this.id = id;
		this.label = info.getLabel();
		this.icon = info.getIcon();
		this.enabled = enabled;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getIcon() {
		return icon;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
}
