package org.riotfamily.common.markup;

import java.io.PrintWriter;

import org.springframework.web.util.HtmlUtils;

/**
 * Utility class to generate markup code. This example ... 
 * <pre>
 * 	TagWriter tag = new TagWriter(writer);
 *  tag.start("div").attribute("id", "foo")
 *      .body("Hello ")
 *      .start("strong").body("World")
 *      .closeAll();
 * </pre>
 * ... will produce the following code:
 * <pre>
 *     &lt;div id="foo"&gt;Hello &lt;strong&gt;World&lt;/strong&gt;&lt;/div&gt;
 * </pre>
 * <p>
 * Note that calling <code>start()</code> on an already opened tag will return
 * a new TagWriter. To create complex nested structures you should better use
 * a {@link org.riotfamily.common.markup.DocumentWriter DocumentWriter} instead.
 * </p>
 */
public class TagWriter {

	private static final int STATE_CLOSED = 0;
	
	private static final int STATE_START = 1;
	
	private static final int STATE_BODY = 2;
	
    private boolean empty = false;

    private String tagName;

    private PrintWriter writer;

    private boolean xhtml = true;
    
    private int state = STATE_CLOSED;
    
    private TagWriter parent;
    
    public TagWriter(PrintWriter writer) {
    	this.writer = writer;
    }

    private TagWriter(TagWriter parent) {
    	this.parent = parent;
    	this.writer = parent.writer;
    	this.xhtml = parent.xhtml;
    }
    
    public void setXhtml(boolean xhtml) {
        this.xhtml = xhtml;
    }

    public TagWriter start(String tagName) {
    	return start(tagName, false);
    }
    
    public TagWriter startEmpty(String tagName) {
    	return start(tagName, true);
    }
    
    public TagWriter start(String tagName, boolean empty) {
    	if (state != STATE_CLOSED) {
    		if (state == STATE_START) {
    			body();
    		}
    		return new TagWriter(this).start(tagName, empty);
    	}
    	else {
	    	this.tagName = tagName;
	    	this.empty = empty;
	    	writer.write('<');
	        writer.write(tagName);
	        state = STATE_START;
	        return this;
    	}
    }
    
    public TagWriter attribute(String name) {
        String value = null;
        if (xhtml) {
            value = name;
        }
        return attribute(name, value, true);
    }

    public TagWriter attribute(String name, int value) {
    	if (state != STATE_START) {
    		throw new IllegalStateException("start() must be called first");
    	}
        writer.write(' ');
        writer.write(name);
        writer.write('=');
        writer.write('"');
        writer.write(String.valueOf(value));
        writer.write('"');
        return this;
    }

    public TagWriter attribute(String name, boolean present) {
        String value = present ? name : null;
        return attribute(name, value, false);
    }
    
    public TagWriter attribute(String name, String value) {
        return attribute(name, value, false);
    }

    public TagWriter attribute(String name, String value, boolean renderEmpty) {
    	if (state != STATE_START) {
    		throw new IllegalStateException("start() must be called first");
    	}
        if (value == null && !renderEmpty) {
            return this;
        }
        writer.write(' ');
        writer.write(name);
        if (value != null) {
            writer.write('=');
            writer.write('"');
            if (value != null) {
            	writer.write(HtmlUtils.htmlEscape(value));
            }
            writer.write('"');
        }
        return this;
    }

    public TagWriter body() {
    	if (state != STATE_START) {
    		throw new IllegalStateException("start() must be called first");
    	}
        if (empty) {
            throw new IllegalStateException("Body not allowed for empty tags");
        }
        writer.write('>');
        state = STATE_BODY;
        return this;
    }
    
    public TagWriter body(String body) {
    	return body(body, true);
    }
    
    public TagWriter body(String body, boolean escapeHtml) {
        body();
        if (body != null) {
        	if (escapeHtml) {
        		writer.write(HtmlUtils.htmlEscape(body));
        	}
        	else {
        		writer.write(body);
        	}
        }
        return this;
    }
    
    public TagWriter cDataBody(String body) {
        body();
        if (body != null) {
        	writer.write("<![CDATA[\n");
            writer.write(body);
            writer.write("\n]]>");
        }
        return this;
    }
    
    public TagWriter print(String s) {
    	if (state != STATE_BODY) {
    		throw new IllegalStateException("body() must be called first");
    	}
    	writer.print(HtmlUtils.htmlEscape(s));
    	return this;
    }
    
    public TagWriter println(String s) {
    	if (state != STATE_BODY) {
    		throw new IllegalStateException("body() must be called first");
    	}
    	writer.println(HtmlUtils.htmlEscape(s));
    	return this;
    }
    
    public TagWriter end() {
    	if (state == STATE_CLOSED) {
    		throw new IllegalStateException("Tag alredy closed");
    	}
        if (empty) {
            if (xhtml) {
                writer.write('/');
            }
            writer.write('>');
        }
        else {
        	if (state != STATE_BODY) {
        		body();
        	}
            writer.write('<');
            writer.write('/');
            writer.write(tagName);
            writer.write('>');
        }
        state = STATE_CLOSED;
        return parent != null ? parent : this;
    }
    
    public void closeAll() {
    	TagWriter writer = this;
    	while (writer != null) {
    		writer.end();
    		writer = writer.parent;
    	}
    }

}