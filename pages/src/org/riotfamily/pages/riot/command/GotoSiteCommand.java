package org.riotfamily.pages.riot.command;

import org.riotfamily.pages.mapping.PageUrlBuilder;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.core.PopupCommand;

public class GotoSiteCommand extends PopupCommand {

	public static final String STYLE_CLASS = "link";

	private PageUrlBuilder pageUrlBuilder;

	public GotoSiteCommand(PageUrlBuilder pageUrlBuilder) {
		this.pageUrlBuilder = pageUrlBuilder;
	}
	
	protected String getUrl(CommandContext context) {
		Site site = (Site) context.getBean();
		StringBuffer url = pageUrlBuilder.getAbsoluteSiteUrl(site,
				context.getRequest()).append("/");
		
		return url.toString();
	}
	
	protected String getStyleClass(CommandContext context, String action) {
		return STYLE_CLASS;
	}

}
