package org.riotfamily.cachius.http.content;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.http.support.IOUtils;


public class CharacterContent implements Content {

	private File file;
	
	public CharacterContent(File file) {
		this.file = file;
	}

	public int getLength(HttpServletRequest request, HttpServletResponse response) {
		//REVISIT Return correct number of bytes based on the actual response encoding (which may not be UTF-8)
		return (int) file.length();
	}

	public void serve(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		IOUtils.serve(file, response.getWriter(), "UTF-8");
	}

	public void delete() {
		file.delete();
	}
	
}
