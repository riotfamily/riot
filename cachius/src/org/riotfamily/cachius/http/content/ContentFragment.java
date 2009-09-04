package org.riotfamily.cachius.http.content;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ContentFragment extends Serializable {

public int getLength(HttpServletRequest request, HttpServletResponse response);
	
	public void serve(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;
}
