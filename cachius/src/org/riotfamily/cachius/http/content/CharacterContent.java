package org.riotfamily.cachius.http.content;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.http.support.IOUtils;


public class CharacterContent implements Content {

	private File file;
	
	private String encoding;
	
	public CharacterContent(File file, String encoding) {
		this.file = file;
		this.encoding = encoding;
	}

	public int getLength(HttpServletRequest request, HttpServletResponse response) {
		return (int) file.length();
	}

	public void serve(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		IOUtils.serve(file, response.getWriter(), encoding);
	}

	public void delete() {
		file.delete();
	}
	
}
