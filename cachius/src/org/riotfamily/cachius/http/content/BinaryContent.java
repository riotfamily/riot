package org.riotfamily.cachius.http.content;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.http.support.IOUtils;


public class BinaryContent implements Content {

	private File file;
	
	public BinaryContent(File file) {
		this.file = file;
	}

	public int getLength(HttpServletRequest request, HttpServletResponse response) {
		return (int) file.length();
	}

	public void serve(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		IOUtils.serve(file, response.getOutputStream());
	}

	public void delete() {
		file.delete();
	}
	
}
