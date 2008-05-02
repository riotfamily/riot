package org.riotfamily.common.web.ui;

import java.io.PrintWriter;
import java.util.Locale;

import org.springframework.util.StringUtils;

public class LocaleRenderer implements ObjectRenderer {

	public void render(Object obj, RenderContext context, PrintWriter writer) {
		if (obj != null) {
			Locale inLocale = context.getMessageResolver().getLocale();
			if (obj instanceof Locale) {
				Locale locale = (Locale) obj;
				writer.print(locale.getDisplayName(inLocale));
			}
			else {
				String s = obj.toString();
				Locale locale = StringUtils.parseLocaleString(s);
				writer.print(locale.getDisplayName(inLocale));
			}
		}
	}

}
