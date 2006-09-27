package org.riotfamily.search.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

public class PageData {

    private static final int BUFFER_SIZE = 1024;
    
    private String url;

    private String html;
    
    private String redirectUrl;
    
    private String error;
    
    
    public PageData(String url) {
		this.url = url;
	}

	public void setContent(InputStream in, String charset) throws IOException {
        Reader reader = new InputStreamReader(in, charset);
        StringWriter writer = new StringWriter();
        int charCount = 0;
		char[] buffer = new char[BUFFER_SIZE];
		int charsRead = -1;
		while ((charsRead = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, charsRead);
			charCount += charsRead;
		}
		this.html = writer.toString();
    }

    public String getHtml() {
        return html;
    }
    
    public String getUrl() {
		return this.url;
	}

	public boolean isOk() {
    	return html != null && error == null;
    }
	
	public boolean isRedirect() {
    	return redirectUrl != null;
    }

	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}

		
}
