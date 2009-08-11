package org.riotfamily.website.performance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class MinifyCssController extends AbstractMinifyController {

	private YUICssCompressor compressor = new YUICssCompressor();
	
	protected String getContentType() {
		return "text/css";
	}
	
	protected Compressor getCompressor() {
		return compressor;
	}
	
	@Override
	protected void capture(String path, ByteArrayOutputStream buffer,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String media = null;
		int i = path.lastIndexOf('@');
		if (i != -1) {
			media = path.substring(i + 1);
			path = path.substring(0, i);
		}
		if (media != null) {
			PrintWriter out = new PrintWriter(buffer);
			out.write("@media ");
			out.write(media);
			out.write(" {\n");
			out.flush();
			super.capture(path, buffer, request, response);
			out.write("}\n");
			out.flush();
		}
		else {
			super.capture(path, buffer, request, response);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String buildParam(Collection<?> sheets) {
		StringBuilder param = new StringBuilder();
		Iterator<?> it = sheets.iterator();
		while (it.hasNext()) {
			Object sheet = it.next();
			if (sheet instanceof Map) {
				Map<String, String> map = (Map<String, String>) sheet;
				param.append(map.get("href"));
				String media = map.get("media");
				if (media != null) {
					param.append('@').append(media);	
				}
			}
			else {
				param.append(sheet);
			}
			if (it.hasNext()) {
				param.append(',');
			}
		}
		return param.toString();
	}
	
}
