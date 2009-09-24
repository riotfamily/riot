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
package org.riotfamily.core.resource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.mapping.HandlerUrlUtils;
import org.riotfamily.common.performance.Compressor;
import org.riotfamily.common.servlet.ServletUtils;
import org.springframework.core.io.Resource;

/**
 * Controller that serves an internal resource.
 */
public class ResourceController extends AbstractResourceController {

	private Map<String, Compressor> compressors;

	public void setCompressors(Map<String, Compressor> compressors) {
		this.compressors = compressors;
	}
	
	@Override
	protected String getResourcePath(HttpServletRequest request) {
		return "/" + HandlerUrlUtils.getPathWithinMapping(request);
	}
	
	protected Reader getReader(Resource res, String path, String contentType,
			HttpServletRequest request) throws IOException {
		
		Reader in = super.getReader(res, path, contentType, request);
		if (compressors != null && ServletUtils.isDirectRequest(request)) {
			Compressor compressor = compressors.get(contentType);
			if (compressor != null) {
				StringWriter buffer = new StringWriter();
				compressor.compress(in, buffer);
				in = new StringReader(buffer.toString());
			}
		}
		return in;
	}

}
