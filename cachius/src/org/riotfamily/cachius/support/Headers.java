package org.riotfamily.cachius.support;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class Headers implements Serializable {

	private SimpleDateFormat format = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
	
	private ArrayList<Header> headers = new ArrayList<Header>();
	
	public Headers() {
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public void add(String name, String value) {
		getHeader(name).addValue(value);
	}
	
	public void addInt(String name, int value) {
		add(name, String.valueOf(value));
	}

	public void addDate(String name, long date) {
		add(name, format.format(new Date(date)));
	}
	
	public void set(String name, String value) {
		getHeader(name).setValue(value);
	}
	
	public void setInt(String name, int value) {
		set(name, String.valueOf(value));
	}

	public void setDate(String name, long date) {
		set(name, format.format(new Date(date)));
	}

	public void clear() {
		headers.clear();
	}
	
	public boolean contain(String name) {
		for (Header header : headers) {
			if (header.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	private Header getHeader(String name) {
		for (Header header : headers) {
			if (header.getName().equalsIgnoreCase(name)) {
				return header;
			}
		}
		Header header = new Header(name);
		headers.add(header);
		return header;
	}
	
	public void remove(String name) {
		Iterator<Header> it = headers.iterator();
		while (it.hasNext()) {
			Header header = it.next();
			if (header.getName().equalsIgnoreCase(name)) {
				it.remove();
			}
		}
	}
	
	public void addToResponse(HttpServletResponse response) {
		for (Header header : headers) {
			header.addToResponse(response);
		}
	}
	
	private static class Header implements Serializable {
		
		private String name;
		
		private ArrayList<String> values = new ArrayList<String>();

		public Header(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setValue(String value) {
			values.clear();
			addValue(value);
		}
		
		public void addValue(String value) {
			values.add(value);
		}
		
		public void addToResponse(HttpServletResponse response) {
			for (String value : values) {
				response.addHeader(name, value);	
			}
		}
		
	}
}
