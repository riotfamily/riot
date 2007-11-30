/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.resource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.compressor.Compressor;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class JoiningResourceController extends AbstractResourceController {

	private static Log log = LogFactory.getLog(JoiningResourceController.class);
	
	private Compressor compressor;
	
	private String contentType;
	
	private boolean shouldBeZipped;
	
	public void setCompressor(Compressor compressor) {
		this.compressor = compressor;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
		shouldBeZipped = contentTypeShouldBeZipped(contentType);
	}

	public boolean gzipResponse(HttpServletRequest request) {
		return shouldBeZipped;
	}
	
	private String normalizePath(String path) {
		if (path.startsWith("/")) {
			return path;
		}
		return "/" + path;
	}
	
	protected long getLastModified(String path) {
		long lastModified = -1;
		String[] paths = StringUtils.commaDelimitedListToStringArray(path);
		for (int i = 0; i < paths.length; i++) {
			long mtime = super.getLastModified(normalizePath(paths[i]));
			if (mtime > lastModified) {
				lastModified = mtime;
			}
		}
		return lastModified;
	}
	
	protected boolean serveResource(String path, HttpServletRequest request, 
			HttpServletResponse response)
			throws IOException {

		response.setContentType(contentType);
		StringWriter buffer = new StringWriter();
		String[] paths = StringUtils.commaDelimitedListToStringArray(path);
		for (int i = 0; i < paths.length; i++) {
			path = normalizePath(paths[i]);
			Resource res = lookupResource(path);
			if (res != null) {
				if (!contentType.equals(getContentType(res))) {
					log.warn("Unexpected contentType for " + res.getFilename());
				}
				serveText(res, path, contentType, request, buffer);
				buffer.write("\n");
			}
			else {
				log.error("Resource not found: " + path);
			}
		}
		StringReader in = new StringReader(buffer.toString());
		Writer out = response.getWriter();
		if (compressor != null) {
			compressor.compress(in, out);
		}
		else {
			FileCopyUtils.copy(in, out);
		}
		return true;
	}
	
}
