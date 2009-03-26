package org.riotfamily.media.controller;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.media.store.FileStore;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class DownloadController implements Controller {

	private RiotLog log = RiotLog.get(this);
	
	private	FileStore fileStore;
	
	public DownloadController(FileStore fileStore) {
		this.fileStore = fileStore;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String uri = (String) request.getAttribute("uri");
		if (uri != null) {
			File file = fileStore.retrieve(uri);
			log.debug("Serving file "+uri+" with content-disposition: attachment");
			if (file != null && file.canRead()) {
				response.setHeader("Content-Disposition", "attachment");
				response.setContentLength((int) file.length());
				IOUtils.serve(new FileInputStream(file), 
						response.getOutputStream());

				return null;
			}
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return null;
	}

}
