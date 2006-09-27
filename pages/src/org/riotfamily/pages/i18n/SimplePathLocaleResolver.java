package org.riotfamily.pages.i18n;

/**
 * Simple LocaleResolver implementation that interprets the first component
 * of the requested path as language.  
 */
public class SimplePathLocaleResolver extends AbstractPathLocaleResolver {

	protected String resolveLanguage(String path) {
		if (path == null || path.length() < 3 || path.charAt(0) != '/') {
			return null;
		}
		if (path.length() == 3 || path.charAt(3) == '/' || 
				(path.charAt(3) == '.' && path.indexOf('/', 1) == -1)) {
			
			return path.substring(1, 3);
		}
		return null;
	}

}
