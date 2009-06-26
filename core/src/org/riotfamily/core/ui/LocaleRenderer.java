package org.riotfamily.core.ui;

import java.io.PrintWriter;
import java.util.Locale;

import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.web.ui.RenderContext;
import org.springframework.util.StringUtils;

public class LocaleRenderer extends CountryFlagRenderer {

	public LocaleRenderer(String resourcePath) {
		super(resourcePath);
	}

	public void render(Object obj, RenderContext context, PrintWriter writer) {
		if (obj instanceof Locale) {
			TagWriter span = new TagWriter(writer).start("span")
					.attribute("class", "locale").body();
			
			Locale locale = (Locale) obj;
			if (StringUtils.hasLength(locale.getCountry())) {
				renderFlag(locale.getCountry(), null, context, writer);
			}
			else {
				renderFlag(getInternationalFlag(), null, context, writer);	
			}
			writer.print(locale.getDisplayName(context.getMessageResolver().getLocale()));
			span.end();
		}
	}
}
