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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.resource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.compressor.Compressor;
import org.springframework.core.io.Resource;

/**
 * Controller that serves an internal resource.
 * <p>
 * Note: This will only work when a prefix mapping is used for the 
 * DispatcherServlet (like <tt>/riot/*</tt>) since 
 * <code>request.getPathInfo()</code> is used.
 * </p>
 */
public class ResourceController extends AbstractResourceController {

	private Map compressors;
	
	public void setCompressors(Map compressors) {
		this.compressors = compressors;
	}
	
	protected Reader getReader(Resource res, String path, String contentType,
			HttpServletRequest request) throws IOException {
		
		Reader in = super.getReader(res, path, contentType, request);
		if (compressors != null) {
			Compressor compressor = (Compressor) compressors.get(contentType);
			if (compressor != null) {
				StringWriter buffer = new StringWriter();
				compressor.compress(in, buffer);
				in = new StringReader(buffer.toString());
			}
		}
		return in;
	}

}
