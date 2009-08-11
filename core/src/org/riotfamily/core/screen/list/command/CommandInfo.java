package org.riotfamily.core.screen.list.command;


public class CommandInfo {

	private String action;
	
	private String label;
	
	private String icon;

	private boolean showOnForm;
	
	public CommandInfo(String action, String label, String icon,
			boolean showOnForm) {
		
		this.action = action;
		this.label = label;
		this.icon = icon;
		this.showOnForm = showOnForm;
	}

	public String getAction() {
		return action;
	}
	
	public String getLabel() {
		return label;
	}

	public String getIcon() {
		return icon;
	}

	public boolean isShowOnForm() {
		return showOnForm;
	}
	
}
