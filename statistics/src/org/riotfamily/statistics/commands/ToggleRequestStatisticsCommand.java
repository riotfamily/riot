package org.riotfamily.statistics.commands;

import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class ToggleRequestStatisticsCommand extends AbstractSwitchCommand  {

	private RequestCountFilterPlugin filterPlugin;
	
	public ToggleRequestStatisticsCommand(RequestCountFilterPlugin filterPlugin) {
		this.filterPlugin = filterPlugin;
	}

	@Override
	protected boolean isEnabled() {
		return filterPlugin.isEnabled();
	}
	
	@Override
	protected void setEnabled(boolean enabled) {
		filterPlugin.setEnabled(enabled);
	}

}
