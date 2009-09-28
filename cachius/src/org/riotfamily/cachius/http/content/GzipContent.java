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
package org.riotfamily.cachius.http.content;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.http.support.IOUtils;


public class GzipContent extends BinaryContent {

	private static Pattern IE_MAJOR_VERSION_PATTERN = 
			Pattern.compile("^Mozilla/\\d\\.\\d+ \\(compatible[-;] MSIE (\\d)");

	private static Pattern BUGGY_NETSCAPE_PATTERN = 
			Pattern.compile("^Mozilla/4\\.0[678]");


	private File zipFile;
	
	public GzipContent(File file, File zipFile) throws IOException {
		super(file);
		this.zipFile = zipFile;
		InputStream in = new BufferedInputStream(new FileInputStream(file));
    	OutputStream out = new GZIPOutputStream(new FileOutputStream(zipFile));
    	IOUtils.copy(in, out);
    	IOUtils.closeStream(out);
	}

	@Override
	public int getLength(HttpServletRequest request,
			HttpServletResponse response) {
		
		if (responseCanBeZipped(request)) {
			return (int) zipFile.length();
		}
		return super.getLength(request, response);
	}
	
	@Override
	public void serve(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setHeader("Vary", "Accept-Encoding, User-Agent");
		if (responseCanBeZipped(request)) {
			serveZipped(request, response);
		}
		else {
			super.serve(request, response);
		}
	}
	
	protected void serveZipped(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		response.setHeader("Content-Encoding", "gzip");
		IOUtils.serve(zipFile, response.getOutputStream());	
	}
	
	@Override
	public void delete() {
		super.delete();
		zipFile.delete();
	}

	/**
	 * Checks whether the response can be compressed. This is the case when
	 * {@link #clientAcceptsGzip(HttpServletRequest) the client accepts gzip 
	 * encoded content}, the {@link #userAgentHasGzipBugs(HttpServletRequest) 
	 * user-agent has no known gzip-related bugs} and the request is not an 
	 * include request.
	 */
	protected boolean responseCanBeZipped(HttpServletRequest request) {
		return clientAcceptsGzip(request) 
				&& !userAgentHasGzipBugs(request)
				&& request.getAttribute("javax.servlet.include.request_uri") == null;
	}
	
	/**
	 * Returns whether the Accept-Encoding header contains "gzip".
	 */
	@SuppressWarnings("unchecked")
	protected boolean clientAcceptsGzip(HttpServletRequest request) {
		Enumeration values = request.getHeaders("Accept-Encoding");
		if (values != null) {
			while (values.hasMoreElements()) {
				String value = (String) values.nextElement();
				if (value.indexOf("gzip") != -1) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns whether the User-Agent has known gzip-related bugs. This is true
	 * for Internet Explorer &lt; 6.0 SP2 and Mozilla 4.06, 4.07 and 4.08. The
	 * method will also return true if the User-Agent header is not present or
	 * empty.
	 */
	protected boolean userAgentHasGzipBugs(HttpServletRequest request) {
		String ua = request.getHeader("User-Agent");
		if (ua == null || ua.length() == 0) {
			return true;
		}
		Matcher m = IE_MAJOR_VERSION_PATTERN.matcher(ua);
		if (m.find()) {
			int major = Integer.parseInt(m.group(1));
			if (major > 6) {
				// Bugs are fixed in IE 7 
				return false;
			}
			if (ua.indexOf("Opera") != -1) {
				// Opera has no known gzip bugs
				return false;
			}
			if (major == 6) {
				// Bugs are fixed in Service Pack 2 
				return ua.indexOf("SV1") == -1;
			}
			// All other version are buggy.
			return true;
		}
		return BUGGY_NETSCAPE_PATTERN.matcher(ua).find();
	}
}
