package org.riotfamily.common.util;

public final class ResourceUtils {

	private static final String CLASSPATH_PREFIX = "classpath:";
	
	private ResourceUtils() {
	}
	
	public static String getPath(Class clazz, String name) {
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

}
