package org.riotfamily.linkcheck;

import java.util.Map;

import org.riotfamily.core.screen.ModelAndViewScreenlet;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.linkcheck.BrokenLink;

public class BrokenLinksScreenlet extends ModelAndViewScreenlet {

	@Override
	protected void populateModel(Map<String, Object> model,
			ScreenContext context) {

		model.put("totalBrokenLinks", BrokenLink.findAllBrokenLinks().size());
	}

}
