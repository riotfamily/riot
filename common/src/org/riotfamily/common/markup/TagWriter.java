/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.markup;

import java.io.PrintWriter;

import org.riotfamily.common.util.FormatUtils;

/**
 * Utility class to generate markup code. This example ...
 * <pre>
 *TagWriter tag = new TagWriter(writer);
 *tag.start("div").attribute("id", "foo")
 *        .body("Hello ")
 *        .start("strong").body("World")
 *        .closeAll();
 * </pre>
 * ... will produce the following code:
 * <pre>
 * &lt;div id="foo"&gt;Hello &lt;strong&gt;World&lt;/strong&gt;&lt;/div&gt;
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

	private static final int STATE_CDATA = 3;

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
			   writer.write(FormatUtils.xmlEscape(value));
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
				writer.write(FormatUtils.xmlEscape(body));
			}
			else {
				writer.write(body);
			}
		}
		return this;
	}

	public TagWriter cData() {
		if (state != STATE_BODY) {
			throw new IllegalStateException("body() must be called first");
		}
		if (state == STATE_CDATA) {
			throw new IllegalStateException(
					"cData() must not be called within a CDATA section");
		}
		writer.write("<![CDATA[\n");
		state = STATE_CDATA;
		return this;
	}

	public TagWriter closeCData() {
		if (state != STATE_CDATA) {
			throw new IllegalStateException("cData() must be called first");
		}
		writer.write("]]>");
		state = STATE_BODY;
		return this;
	}

	public TagWriter print(String s) {
		if (state < STATE_BODY) {
			throw new IllegalStateException("body() must be called first");
		}
		writer.print(state == STATE_CDATA ? s : FormatUtils.xmlEscape(s));
		return this;
	}

	public TagWriter println(String s) {
		print(s);
		writer.println();
		return this;
	}

	public TagWriter println() {
		writer.println();
		return this;
	}

	public TagWriter end() {
		if (state == STATE_CLOSED) {
			throw new IllegalStateException("Tag already closed");
		}
		if (empty) {
			if (xhtml) {
				writer.write('/');
			}
			writer.write('>');
		}
		else {
			if (state == STATE_CDATA) {
				closeCData();
			}
			if (state < STATE_BODY) {
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