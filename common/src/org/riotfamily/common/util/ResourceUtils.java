package org.riotfamily.common.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

/**
 * Provides utility method to work with classpath resources.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public final class ResourceUtils {

	private static final String CLASSPATH_PREFIX = "classpath:";

	private ResourceUtils() {
	}

	public static String getPath(Object object, String name) {
		return getPath(object.getClass(), name);
	}

	public static String getPath(Class<?> clazz, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append(CLASSPATH_PREFIX).append('/');
		String s = clazz.getName();
		s = s.substring(0, s.lastIndexOf('.'));
		s = s.replace('.', '/');
		sb.append(s);
		sb.append('/');
		sb.append(name);
		return sb.toString();
	}

	public static String readResource(Resource resource, String encoding)
			throws UnsupportedEncodingException, IOException {

		Reader in = new InputStreamReader(resource.getInputStream(), encoding);
		return FileCopyUtils.copyToString(in);
	}

}
