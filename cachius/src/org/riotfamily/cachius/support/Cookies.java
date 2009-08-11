package org.riotfamily.cachius.support;

import java.io.Serializable;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class Cookies implements Serializable {

	private ArrayList<SerializableCookie> cookies;
	
	public void add(Cookie cookie) {
		if (cookies == null) {
			cookies = new ArrayList<SerializableCookie>();
		}
		cookies.add(new SerializableCookie(cookie));
	}
	
	public void clear() {
		cookies = null;
	}
	
	public void addToResponse(HttpServletResponse response) {
		if (cookies != null) {
			for (SerializableCookie cookie : cookies) {
				response.addCookie(cookie.create());
			}
		}
	}
}
