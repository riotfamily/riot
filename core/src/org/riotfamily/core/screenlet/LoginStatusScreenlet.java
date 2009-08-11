package org.riotfamily.core.screenlet;

import java.util.Map;

import org.riotfamily.core.screen.ModelAndViewScreenlet;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.security.session.LoginManager;

public class LoginStatusScreenlet extends ModelAndViewScreenlet {

	@Override
	protected void populateModel(Map<String, Object> model,
			ScreenContext context) {

		model.put("sessionData", LoginManager.getSessionMetaData(context.getRequest()));
	}

}
