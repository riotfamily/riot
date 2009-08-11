package org.riotfamily.website.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.RiotLog;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ClientErrorLogController implements Controller {

	private RiotLog log = RiotLog.get(this);
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String uri = ServletUtils.getRequestUri(request);
		String extension = FormatUtils.getExtension(uri);
		if (extension.equals("js")) {
			response.setContentType("text/javascript");
			ServletUtils.setFarFutureExpiresHeader(response);
			PrintWriter out = response.getWriter();
			out.print("(function(){var h=window.onerror;window.onerror=function(e,f,l){"
					+ "new Image().src='" + FormatUtils.stripExtension(uri)
					+ ".gif?error='+escape(e)+'&file='+escape(f)+'&line='+escape(l);"
					+ "if(h)return h(e,f,l)}})()");
			
			return null;
		}
		
		if (request.getParameter("error") != null) {
			log.error("Error in %s (line %s): %s",
					request.getParameter("file"),
					request.getParameter("line"),		
					request.getParameter("error"));
		}
		ServletUtils.serveTransparentPixelGif(response);
		return null;
	}

}
