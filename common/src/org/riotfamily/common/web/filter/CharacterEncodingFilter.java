package org.riotfamily.common.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter that allows to specify a default character encoding for 
 * requests and responses.
 * 
 * If don't want to modify the response encoding use the {@link 
 * org.springframework.web.filter.CharacterEncodingFilter 
 * CharacterEncodingFilter} provied by Spring. 
 */
public class CharacterEncodingFilter extends OncePerRequestFilter {

	private String encoding;

	private boolean forceRequestEncoding;
	
	private boolean forceResponseEncoding;

	/**
	 * Set the encoding to use for requests. This encoding will be passed into a
	 * ServletRequest.setCharacterEncoding call.
	 * <p>
	 * Whether this encoding will override existing request encodings depends on
	 * the "forceEncoding" flag.
	 * 
	 * @see javax.servlet.ServletRequest#setCharacterEncoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Set whether the encoding of this filter should override existing request
	 * encodings. Default is false, i.e. do not modify encoding if
	 * ServletRequest.getCharacterEncoding returns a non-null value.
	 * 
	 * @see #setEncoding
	 * @see javax.servlet.ServletRequest#getCharacterEncoding
	 */
	public void setForceRequestEncoding(boolean forceEncoding) {
		this.forceRequestEncoding = forceEncoding;
	}
	
	/**
	 * Set whether the encoding of this filter should override existing response
	 * encodings.
	 * 
	 * @see #setEncoding
	 * @see javax.servlet.ServletRequest#getCharacterEncoding
	 */
	public void setForceResponseEncoding(boolean forceEncoding) {
		this.forceResponseEncoding = forceEncoding;
	}

	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (this.forceRequestEncoding || request.getCharacterEncoding() == null) {
			request.setCharacterEncoding(this.encoding);
		}
		filterChain.doFilter(request, new EncodingResponseWrapper(response));
	}

	private class EncodingResponseWrapper extends
			HttpServletResponseWrapper {

		private boolean encodingSpecified;

		public EncodingResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		public void setCharacterEncoding(String encoding) {
			if (forceResponseEncoding) {
				super.setCharacterEncoding(encoding);	
			}
			else {
				super.setCharacterEncoding(encoding);
			}
			encodingSpecified = true;
		}
		
		public void setContentType(String type) {
			String mimeType = null;
			String charset = null;
			int i = type.indexOf(';');
			if (i != -1) {
				mimeType = type.substring(0, i).trim().toLowerCase();
				i = type.indexOf('=', i);
				charset = type.substring(i + 1).trim();
			}
			else {
				mimeType = type.trim().toLowerCase();
			}
			
			if (mimeType.startsWith("text/") &&
					(forceResponseEncoding || (charset == null && !encodingSpecified))) {
				
				charset = encoding;
			}
			
			String explicitType = mimeType;
			if (charset != null) {
				explicitType += "; charset=" + charset;
				encodingSpecified = true;
			}
			super.setContentType(explicitType);
		}

	}
}
