package org.riotfamily.core.resource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.website.performance.Compressor;
import org.springframework.core.io.Resource;

/**
 * Controller that serves an internal resource.
 */
public class ResourceController extends AbstractResourceController {

	private Map<String, Compressor> compressors;

	public void setCompressors(Map<String, Compressor> compressors) {
		this.compressors = compressors;
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
