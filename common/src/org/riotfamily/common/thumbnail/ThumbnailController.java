package org.riotfamily.common.thumbnail;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.file.FileStore;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ThumbnailController implements Controller {
	
	private Thumbnailer thumbnailer;
	
	private FileStore fileStore;
	
	public void setFileStore(FileStore fileStore) {
		this.fileStore = fileStore;
	}
	
	public void setThumbnailer(Thumbnailer thumbnailer) {
		this.thumbnailer = thumbnailer;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		String fileName = ServletRequestUtils.getStringParameter(request, "sourceFile");
		File file = fileStore.retrieve(fileName);
		if (file != null && file.exists()) {
			thumbnailer.renderThumbnail(file, null, response.getOutputStream());
		}
		return null;
	}	
	
}
