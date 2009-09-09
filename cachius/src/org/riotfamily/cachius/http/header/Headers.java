/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.cachius.http.header;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class Headers implements Serializable {

	private static final long serialVersionUID = 667477578909492604L;

	private SimpleDateFormat format = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
	
	private ArrayList<Header> headers = new ArrayList<Header>();
	
	public Headers() {
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public void add(String name, String value) {
		getHeader(name).addValue(createValue(value));
	}
	
	public void addInt(String name, int value) {
		addString(name, String.valueOf(value));
	}

	public void addDate(String name, long date) {
		addString(name, format.format(new Date(date)));
	}
	
	private void addString(String name, String value) {
		getHeader(name).addValue(new StaticHeaderValue(value));
	}
	
	public void set(String name, String value) {
		getHeader(name).setValue(createValue(value));
	}

	public void setInt(String name, int value) {
		setString(name, String.valueOf(value));
	}

	public void setDate(String name, long date) {
		setString(name, format.format(new Date(date)));
	}
	
	private void setString(String name, String value) {
		getHeader(name).setValue(new StaticHeaderValue(value));
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
	
	private HeaderValue createValue(String value) {
		int i = value.indexOf("(@sessionid)");
		if (i >= 0) {
			return new SessionIdHeaderValue(value, i, 12);
		}
		return new StaticHeaderValue(value);
	}
	
	public void send(HttpServletRequest request, HttpServletResponse response) {
		for (Header header : headers) {
			header.send(request, response);
		}
	}
}
