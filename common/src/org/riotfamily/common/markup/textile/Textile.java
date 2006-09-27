/**

Original PHP Header:
_____________
T E X T I L E

A Humane Web Text Generator

Version 2.0 beta

Copyright (c) 2003-2004, Dean Allen <dean@textism.com>
All rights reserved.

Thanks to Carlo Zottmann <carlo@g-blog.net> for refactoring 
Textile's procedural code into a class framework

_____________
L I C E N S E

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name Textile nor the names of its contributors may be used to
  endorse or promote products derived from this software without specific
  prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

_________
U S A G E

Block modifier syntax:

	Header: h(1-6).
	Paragraphs beginning with 'hn. ' (where n is 1-6) are wrapped in header tags.
	Example: h1. Header... -> <h1>Header...</h1>

	Paragraph: p. (also applied by default)
	Example: p. Text -> <p>Text</p>

	Blockquote: bq.
	Example: bq. Block quotation... -> <blockquote>Block quotation...</blockquote>

	Blockquote with citation: bq.:http://citation.url
	Example: bq.:http://textism.com/ Text...
	->	<blockquote cite="http://textism.com">Text...</blockquote>

	Footnote: fn(1-100).
	Example: fn1. Footnote... -> <p id="fn1">Footnote...</p>

	Numeric list: #, ##
	Consecutive paragraphs beginning with # are wrapped in ordered list tags.
	Example: <ol><li>ordered list</li></ol>

	Bulleted list: *, **
	Consecutive paragraphs beginning with * are wrapped in unordered list tags.
	Example: <ul><li>unordered list</li></ul>

Phrase modifier syntax:

           _emphasis_   ->   <em>emphasis</em>
           __italic__   ->   <i>italic</i>
             *strong*   ->   <strong>strong</strong>
             **bold**   ->   <b>bold</b>
         ??citation??   ->   <cite>citation</cite>
       -deleted text-   ->   <del>deleted</del>
      +inserted text+   ->   <ins>inserted</ins>
        ^superscript^   ->   <sup>superscript</sup>
          ~subscript~   ->   <sub>subscript</sub>
               @code@   ->   <code>computer code</code>
          %(bob)span%   ->   <span class="bob">span</span>

        ==notextile==   ->   leave text alone (do not format)

       "linktext":url   ->   <a href="url">linktext</a>
 "linktext(title)":url  ->   <a href="url" title="title">linktext</a>

           !imageurl!   ->   <img src="imageurl" />
  !imageurl(alt text)!  ->   <img src="imageurl" alt="alt text" />
    !imageurl!:linkurl  ->   <a href="linkurl"><img src="imageurl" /></a>

ABC(Always Be Closing)  ->   <acronym title="Always Be Closing">ABC</acronym>


Table syntax:

	Simple tables:

        |a|simple|table|row|
        |And|Another|table|row|

        |_. A|_. table|_. header|_.row|
        |A|simple|table|row|

    Tables with attributes:

        table{border:1px solid black}.
        {background:#ddd;color:red}. |{}| | | |


Applying Attributes:

    Most anywhere Textile code is used, attributes such as arbitrary css style,
    css classes, and ids can be applied. The syntax is fairly consistent.

    The following characters quickly alter the alignment of block elements:

        <  ->  left align    ex. p<. left-aligned para
        >  ->  right align       h3>. right-aligned header 3
        =  ->  centred           h4=. centred header 4
        <> ->  justified         p<>. justified paragraph

    These will change vertical alignment in table cells:

        ^  ->  top         ex. |^. top-aligned table cell|
        -  ->  middle          |-. middle aligned|
        ~  ->  bottom          |~. bottom aligned cell|

    Plain (parentheses) inserted between block syntax and the closing dot-space
    indicate classes and ids:

        p(hector). paragraph -> <p class="hector">paragraph</p>

        p(#fluid). paragraph -> <p id="fluid">paragraph</p>

        (classes and ids can be combined)
        p(hector#fluid). paragraph -> <p class="hector" id="fluid">paragraph</p>

    Curly {brackets} insert arbitrary css style

        p{line-height:18px}. paragraph -> <p style="line-height:18px">paragraph</p>

        h3{color:red}. header 3 -> <h3 style="color:red">header 3</h3>

    Square [brackets] insert language attributes

        p[no]. paragraph -> <p lang="no">paragraph</p>

        %[fr]phrase% -> <span lang="fr">phrase</span>

    Usually Textile block element syntax requires a dot and space before the block
    begins, but since lists don't, they can be styled just using braces

        #{color:blue} one  ->  <ol style="color:blue">
        # big                   <li>one</li>
        # list                  <li>big</li>
                                <li>list</li>
                               </ol>

	Using the span tag to style a phrase

        It goes like this, %{color:red}the fourth the fifth%
              -> It goes like this, <span style="color:red">the fourth the fifth</span>

*/

package org.riotfamily.common.markup.textile;

import java.util.ArrayList;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.util.StringUtils;


public class Textile extends PhpSupport {
		
    private static final String hlgn = "(?:\\<(?!>)|(?<!<)\\>|\\<\\>|\\=|[()]+)";
    private static final String vlgn = "[\\-^~]";
    
    private static final String clas = "(?:\\([^)]+\\))";
    private static final String lnge = "(?:\\[[^\\]]+\\])";
    private static final String styl = "(?:\\{[^}]+\\})";
    
    private static final String cspn = "(?:\\\\\\\\\\d+)";
    private static final String rspn = "(?:\\/\\d+)";
    
    private static final String a = "(?:" + hlgn + "?" + vlgn + "?"
    		+ "|" + vlgn + "?" + hlgn + "?)";
    
    private static final String s = "(?:" + cspn + "?" + rspn + "?"
    		+ "|" + rspn + "?" + cspn + "?)";
    
    private static final String c = "(?:" + clas + "?" + styl + "?"
    		 + lnge + "?|" + styl + "?" + lnge + "?" + clas + "?"
    		+ "|" + lnge + "?" + styl + "?" + clas + "?)";
    
    private static final String urlch = 
    		"[\\w\"$\\-_.+!*'(),\";\\/?:@=&%#{}|\\\\^~\\[\\]`]";
    
    
    private static final String LEFT = "left";
    private static final String CENTER = "center";
    private static final String RIGHT = "right";
    private static final String JUSTIFY = "justify";
    
    private static final String TOP = "top";
    private static final String MIDDLE = "middle";
    private static final String BOTTOM = "bottom";
    
    private String rel;
    
    private HttpServletRequest request;
    
    private HttpServletResponse response;
    
    private boolean addBlankTragetToAbsoulteLinks = true;
    
    public Textile(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public void setAddBlankTragetToAbsoulteLinks(
			boolean addBlankTragetToAbsoulteLinks) {
		this.addBlankTragetToAbsoulteLinks = addBlankTragetToAbsoulteLinks;
	}


	public String textileThis(String text) {
    	return textileThis(text, false, false, false, true, null);
    }
    
    public String textileThis(String text, boolean lite, boolean encode, 
    		boolean noimage, boolean strict, String rel) {
    	
        if (rel != null) {
           this.rel = " rel=\"" + rel + "\" ";
        }
        
        text = incomingEntities(text);
        
        if (encode) {
			text = str_replace("x%x%", "&#38;", text);
        	return text;
        } 
        else {
        
	    	if(strict) {
				text = fixEntities(text);
				text = cleanWhiteSpace(text);
			}
	
			text = getRefs(text);
	
			text = noTextile(text);
			text = links(text);
			if (!noimage) {
				text = image(text);
			}
			text = code(text);
			text = span(text);
			//text = footnoteRef(text);
			text = glyphs(text);
			//text = retrieve(text);
	
			if (!lite) {
				text = lists(text);
				text = table(text);
				text = block(text);
			}

			// clean up <notextile>
			text = preg_replace("<\\/?notextile>", "", text);
	
			// turn the temp char back to an ampersand entity
			text = str_replace("x%x%", "&#38;", text);
	
			// just to be tidy
			text = str_replace("<br />", "<br />\n", text);
	
			return text;
      	}
    }

    private String pba(String in) {
    	return pba(in, null);
    }
    
    private String pba(String in, String element) { // "parse block attributes"
    	if (element == null) {
    		element = "";
    	}
        StringBuffer style = new StringBuffer();
        String clazz = null;
        String lang = null;
        String colspan = null;
        String rowspan = null;
        String id = null;

        if (StringUtils.hasLength(in)) {
            String matched = in;
            if ("td".equals(element)) {
            	MatchResult csp = new MatchResult();
                if (preg_match("\\\\\\\\(\\d+)", matched, csp)) {
                	colspan = csp.group(1);
                }
                MatchResult rsp = new MatchResult();
                if (preg_match("\\/(\\d+)", matched, rsp)) {
                	rowspan = rsp.group(1);
                }
                MatchResult vert = new MatchResult();
                if (preg_match("(" + vlgn + ")", matched, vert)) {
                    style.append("vertical-align:");
                    style.append(vAlign(vert.group(1)));
                    style.append(';');
                }
            }

            MatchResult sty = new MatchResult();
            if (preg_match("\\{([^}]*)\\}", matched, sty)) {
                style.append(sty.group(1)).append(';');
                matched = str_replace(sty.group(0), "", matched);
            }

            MatchResult lng = new MatchResult();
            if (preg_match("\\[([^)]+?)\\]", matched, lng)) {
                lang = lng.group(1);
                matched = str_replace(lng.group(0), "", matched);
            }

            MatchResult cls = new MatchResult();
            if (preg_match("\\(([^()]+?)\\)", matched, cls)) {
                clazz = cls.group(1);
                matched = str_replace(cls.group(0), "", matched);
            }

            MatchResult pl = new MatchResult();
            if (preg_match("([(]+)", matched, pl)) {
                style.append("padding-left:");
                style.append(pl.group(1).length());
                style.append("em;");
                matched = str_replace(pl.group(0), "", matched);
            }

            MatchResult pr = new MatchResult();
            if (preg_match("([)]+)", matched, pr)) {
                style.append("padding-right:");
                style.append(pr.group(1).length());
                style.append("em;");
                matched = str_replace(pr.group(0), "", matched);
            }

            MatchResult horiz = new MatchResult();
            if (preg_match("(" + hlgn + ")", matched, horiz)) {
                style.append("text-align:");
                style.append(hAlign(horiz.group(1)));
                style.append(';');
            }
            
            MatchResult ids = new MatchResult();
            if (preg_match("^(.*)#(.*)$", clazz, ids)) {
                id = ids.group(2);
                clazz = ids.group(1);
            }

            StringBuffer sb = new StringBuffer();
            if (style.length() > 0) {
            	sb.append(" style=\"");
            	sb.append(style);
            	sb.append('"');
            }
            if (clazz != null) {
            	sb.append(" class=\"").append(clazz).append('"');
            }
            if (lang != null) {
            	sb.append(" lang=\"").append(lang).append('"');
            }
            if (id != null) {
            	sb.append(" id=\"").append(id).append('"');
            }
            if (colspan != null) {
            	sb.append(" colspan=\"").append(colspan).append('"');
            }
            if (rowspan != null) {
            	sb.append(" rowspan=\"").append(rowspan).append('"');
            }
            return sb.toString();
        }
        return "";
    }

    private String table(String text) {
        text = text + "\n\n";
        return preg_replace_callback("(?sm)^(?:table(_?" + s  + a 
        		+ c + ")\\. ?\n)?^(" + a + c + "\\.? ?\\|.*?\\|)\n\n",
        		
        		new ReplacementCallback() {
        			public String getReplacement(Matcher matcher) {
        				return fTable(matcher);
        			}
        		}, text);
    }


    /**
     * Checks whether the text has text not already enclosed by a block tag.
     */
    private boolean hasRawText(String text) {
        return StringUtils.hasLength(preg_replace("(?s)<(p|blockquote|div|form|" +
        		"table|ul|ol|pre|code|h\\d)[^>]*?>.*</\\1>", "", text));
    }

    private String fTable(Matcher m) {
        String tatts = pba(group(m, 1), "table");
        StringBuffer rows = new StringBuffer();
        String[] _rows = preg_split("(?m)\\|$", group(m, 2), -1, PREG_SPLIT_NO_EMPTY);
        for(int i = 0; i < _rows.hashCode(); i++) { 
        	String row = _rows[i];
        	MatchResult rmtch = new MatchResult();
        	String ratts;
            if (preg_match("(?m)^(" + a + c + "\\. )(.*)", row, rmtch)) {
                ratts = pba(rmtch.group(1), "tr");
                row = rmtch.group(2);
            } 
            else {
            	ratts = "";
            }
            
            StringBuffer cells = new StringBuffer();	
            String[] _row = explode("|", row);
            for(int j = 0; i < _row.length; j++) {
            	String cell = _row[j];
                String ctyp = "d";
                String catts;
                if (preg_match("^_", cell)) {
                	ctyp = "h";
                }
                MatchResult cmtch = new MatchResult();
                if (preg_match("^(_?" + s + a + c + "\\. )(.*)", cell, cmtch)) {
                    catts = pba(cmtch.group(1), "td");
                    cell = cmtch.group(2);
                } 
                else {
                	catts = "";
                }

                if (StringUtils.hasLength(cell)) {
                    cells.append("\t\t\t<t").append(ctyp).append(catts)
                    		.append('>').append(cell).append("</t").append(ctyp)
                    		.append(">\n");
                }
            }
            rows.append("\t\t<tr").append(ratts).append(">\n").append(cells)
            		.append("\t\t</tr>\n");
        }
        return "\t<table" + tatts + ">\n" + rows + "\t</table>\n\n";
    }

    private String lists(String text) {
        return preg_replace_callback("(?sm)^([#*]+?" + c + " .*?)$(?![^#*])",
        		new ReplacementCallback() {
        			public String getReplacement(Matcher matcher) {
        				return fList(matcher);
        			}
        		}, text);
    }


    private String fList(Matcher matcher) {
    	StringBuffer out = new StringBuffer();
    	ArrayList lists = new ArrayList();
        String[] text = explode("\n", matcher.group(0));
        for(int i = 0; i < text.length; i++) {
        	String line = text[i];
            String nextline = i + 1 < text.length ? text[i + 1] : "";
            MatchResult m = new MatchResult();
            
            if (preg_match("(?s)^([#*]+)(" + a + c + ") (.*)$", line, m)) {
            	String tl = m.group(1);
            	String atts = m.group(2);
            	String content = m.group(3);
            	
            	MatchResult _nl = new MatchResult();
                preg_match("^([#*]+)\\s.*", nextline, _nl);
                String nl = _nl.group(1);
                if (nl == null) {
                	nl = "";
                }
                
                if (!lists.contains(tl)) {
                    lists.add(tl);
                    atts = pba(atts);
                    line = "\t<" + lT(tl) + "l" + atts + ">\n\t\t<li>" + content;
                } 
                else {
                    line = "\t\t<li>" + content;
                }

                if(nl.length() <= tl.length()) {
                	line += "</li>";
                }
                while (!lists.isEmpty()) {
                	int _last = lists.size() - 1;
                	String k = (String) lists.get(_last);
                	if (nl.length() < k.length()) {
                		line += "\n\t</" + lT(k) + "l>";
                		if(k.length() > 1) { 
                            line += "</li>\n";
                        }
                		lists.remove(_last);
                	}
                	else {
                		break;
                	}
                }
            }
            out.append(line);
            out.append('\n');
        }
        return out.toString();
    }

    private String lT(String in) {
        return preg_match("^#+", in) ? "o" : "u";
    }

    private String doPBr(String in) {
        return preg_replace_callback("(?s)<(p)([^>]*?)>(.*)(</\\1>)", 
        		new ReplacementCallback() {
		        	public String getReplacement(Matcher matcher) {
		        		return doBr(matcher);
		        	}
		        }, in);
    }

    private String doBr(Matcher m) {
        String content = preg_replace("(.+)(?<!<br>|<br />)\n(?![#*\\s|])", 
        		"$1<br />", m.group(3));
        
        return "<" + m.group(1) + m.group(2) + ">" + content + m.group(4);
    }

    private String block(String text) {
    	StringBuffer out = new StringBuffer();
        boolean pre = false;
        String[] find = new String[] {"bq", "h[1-6]", "fn\\d+", "p"};

        //String[] _blocks = explode("\n\n", text);
        //array_push(lines, " ");
        
        String[] _blocks = preg_split("\n\n", text, -1, 0);
        
        for(int i = 0; i < _blocks.length; i++) {
        	String line = _blocks[i];
            if (preg_match("(?i)<pre>", line)) {
                pre = true;
            }

            for(int j = 0; j < find.length; j++) {
            	String tag = find[j];
            	if (!pre) {
            		line = preg_replace_callback("(?s)^(" + tag + ")(" + a + c 
            				+ ")\\.(?::(\\S+))? (.*)$",
            				new ReplacementCallback() {
            					public String getReplacement(Matcher matcher) {
            						return fBlock(matcher);
            					}
            				}, line);
            	}
            }

            if (hasRawText(line)) {
                line = preg_replace("(?s)^(?!\t|<\\/?pre|<\\/?code|$| )(.*)", 
                		"\t<p>$1</p>", line);
            }

			line = doPBr(line);
            line = preg_replace("<br>", "<br />", line);

            if (preg_match("(?i)<\\/pre>", line)) {
                pre = false;
            }
            out.append(line);
        }
        return out.toString();
    }

    private String fBlock(Matcher m) {
        String tag = group(m, 1);
        String atts = group(m, 2);
        String cite = group(m, 3);
        String content = group(m, 4);
        
        atts = pba(atts);

        /*
        MatchResult fns = new MatchResult();
        if (preg_match("fn(\\d+)", tag, fns)) {
            tag = "p";
            String fns1 = fns.group(1);
            String fnid = empty(fn(fns1)) ? fns.group(1) : fn(fns1);
            atts += " id=\"fn" + fnid + '"';
            content = "<sup>" + fns1 + "</sup>" + content;
        }
        */
        
        String start = "\t<" + tag;
        String end = "</" + tag + '>';
        
        if ("bq".equals(tag)) {
            cite = checkRefs(cite);
            cite = (StringUtils.hasLength(cite)) ? " cite=\"" + cite + '"' : "";
            start = "\t<blockquote" + cite + ">\n\t\t<p";
            end = "</p>\n\t</blockquote>";
        }

        return start + atts + '>' + content + end;
    }


    private String span(String text) {
        String[] qtags = new String[] { "\\*\\*", "\\*", "\\?\\?", "\\-", 
        		"__", "_", "%", "\\+", "~", "\\^" };
        
        String pnct = ".,\"'?!;:";

        for(int i = 0; i < qtags.length; i++) {
        	String f = qtags[i];
            text = preg_replace_callback(
            	"(?:^|(?<=[\\s>" + pnct + "])|([{\\[]))"
                + "(" + f + ")"   // 2: tag
                + "(" + c + ")"   // 3: atts
                + "(?::(\\S+?))?" // 4: cite
                + "([^\\s" + f + "]+?|\\S[^" + f + "]*?[^\\s" + f + "])" // 5: content
                + "([" + pnct + "]*?)" //6: end
                + f
                + "(?:$|([\\]}])|(?=\\p{Punct}{1,2}|\\s))",
                new ReplacementCallback() {
                	public String getReplacement(Matcher matcher) {
                		return fSpan(matcher);
                	}
                }, text);
        }
        return text;
    }

    private String fSpan(Matcher m) {
    	String tag = group(m, 2);
    	String atts = group(m, 3);
    	String cite = group(m, 4);
    	String content = group(m, 5);
    	String end = group(m, 6);
    	
        if ("*".equals(tag)) {
        	tag = "strong";
        }
        else if("**".equals(tag)) {
        	tag = "b";
        }
        else if("??".equals(tag)) {
        	tag = "cite";
        }
        else if("_".equals(tag)) {
        	tag = "em";
        }
        else if("__".equals(tag)) {
        	tag = "i";
        }
        else if("-".equals(tag)) {
        	tag = "del";
        }
        else if("%".equals(tag)) {
        	tag = "span";
        }
        else if("+".equals(tag)) {
        	tag = "ins";
        }
        else if("~".equals(tag)) {
        	tag = "sub";
        }
        else if("^".equals(tag)) {
        	tag = "sup";
        }

        atts = pba(atts);
        if (StringUtils.hasLength(cite)) {
        	atts += "cite=\"" + cite + '"';	
        }
        
        StringBuffer out = new StringBuffer();
        out.append('<').append(tag).append(atts).append('>').append(content)
        		.append(end).append("</").append(tag).append('>');
        
        return out.toString();
    }


    private String links(String text) {
        return preg_replace_callback(
        		"([\\s\\[{(]|\\p{Punct})?"		// pre
	            + '"'							// start
	            + '(' + c + ')'           		// atts
	            + "([^\"]+?)"                   // text
	            + "\\s?"
	            + "(?:\\(([^)]+?)\\)(?=\"))?"	// title
	            + "\":"
	            + '('  + urlch + "+?)"			// url
	            + "(\\/)?"						// slash
	            + "([^\\w\\/;]*?)"				// post
	            + "(?=\\s|$)",
	            new ReplacementCallback() {
	            	public String getReplacement(Matcher matcher) {
	            		return fLink(matcher);
	            	}
	            }, text);
    }


    private String fLink(Matcher m) {
      
    	String pre = group(m, 1);
        String atts = group(m, 2);
        String text = group(m, 3);
        String title = group(m, 4);
        String url = group(m, 5);
        String slash = group(m, 6);
        String post = group(m, 7);
        
        url = checkRefs(url);

        atts = pba(atts);
        if (addBlankTragetToAbsoulteLinks && StringUtils.hasLength(title)) {
        	atts += "title=\"" + htmlspecialchars(title) + '"';
        }

        /*
        if (StringUtils.hasLength(atts)) {
        	atts = shelve(atts);
        }
        */
        
        url = relURL(url);
        if (ServletUtils.isAbsoluteUrl(url)) {
        	atts += " target=\"_blank\"";
        }
       
        StringBuffer out = new StringBuffer();
        out.append(pre).append("<a href=\"").append(url).append(slash)
        		.append('"').append(atts).append(rel).append('>').append(text)
        		.append("</a>").append(post);
        
		return out.toString();

    }

    private String getRefs(String text) {
        return preg_replace_callback("(?<=^|\\s)\\[(.+?)\\]((?:http:\\/\\/|\\/)\\S+?)(?=\\s|$)",
        		new ReplacementCallback() {
        			public String getReplacement(Matcher matcher) {
        				return refs(matcher);
        			}
        		}, text);
    }

    private String refs(Matcher m) {
    	/*
    	String flag = m.group(1);
    	String url = m.group(2);
        urlrefs(flag, url);
        */
        return "";
    }

    private String checkRefs(String text) {
    	//String _ref = urlrefs(text);
        //return StringUtils.hasLength(_ref) ? _ref : text;
    	return text;
    }

    private String relURL(String url) {
    	if (request != null) {
    		url = ServletUtils.resolveUrl(url, request);
    	}
    	if (response != null) {
    		url = response.encodeURL(url);
    	}
    	//TODO Support URL aliases
    	/*
        parts = parse_url(url);
        if ((empty(parts["scheme"]) || $parts["scheme"] == "http") && 
             empty(parts["host"]) && 
             preg_match("^\\w", parts["path"]))
        	
            url = hu.$url;
        */
    	
        return url;
    }

    private String image(String text) {
        return preg_replace_callback(
        	"\\!"						// opening !
            + "(\\<|\\=|\\>)??"			// optional alignment atts
            + '(' + c + ')'				// optional style,class atts
            + "(?:\\. )?"				// optional dot-space
            + "([^\\s(!]+?)"			// presume this is the src
            + "\\s?"					// optional space
            + "(?:\\(([^\\)]+?)\\))?"	// optional title
            + "\\!"						// closing
            + "(?::(\\S+?))?"			// optional href
            + "(?=\\s|$|[\\]})])",		// lookahead: space or end of string
            new ReplacementCallback() {
            	public String getReplacement(Matcher matcher) {
            		return fImage(matcher);
            	}
            }, text);
    }

    private String fImage(Matcher m) {
        String algn = group(m, 1);
        String atts = group(m, 2);
        String url = group(m, 3);
        atts  = pba(atts);
        if (StringUtils.hasLength(algn)) {
        	atts += " align=\"" + iAlign(algn) + '"';
        }
        
        String _m4 = group(m, 4);
        if (StringUtils.hasLength(_m4)) {
        	atts += " title=\"" + _m4 + '"';
        	atts += " alt=\"" + _m4 + '"';
        }
        else {
        	//atts += "alt=\"\"";
        }
        
        /*
        String size = getimagesize(url);
        if (size != null) atts += " $size[3]";
        */
        
        String _m5 = m.group(5);
        String href = (StringUtils.hasLength(_m5)) ? checkRefs(_m5) : "";
        
        url = checkRefs(url);
        url = relURL(url);

        StringBuffer out = new StringBuffer();
        if (StringUtils.hasLength(href)) {
            out.append("<a href=\"").append(href).append("\">");
        }
        out.append("<img src=\"").append(url).append('"')
        		.append(atts).append(" />");
        
        if (StringUtils.hasLength(href)) {
            out.append("</a>");
        }

        return out.toString();
    }

    private String code(String text) {
        return preg_replace_callback(
            "(?:^|(?<=[\\s\\(])|([\\[{]))"	// before
            + "@"
            + "(?:\\|(\\w+?)\\|)?"			// lang
            + "(.+?)"						// code
            + "@"
            + "(?:$|([\\]}])|"
            + "(?=\\p{Punct}{1,2}|"
            + "\\s|$))",					// after
            new ReplacementCallback() {
            	public String getReplacement(Matcher matcher) {
            		return fCode(matcher);
            	}
            }, text);
    }

    private String fCode(Matcher m) {
        String before = group(m, 1);
        String lang = group(m, 2);
        String code = group(m, 3);
        String after = group(m, 4);
        if (StringUtils.hasLength(lang)) {
        	lang = " language=\"" + lang + '"';
        }
        return before + "<code" + lang + '>' + code + "</code>" + after;
    }

    /*
    private String shelve(String val) {
        shelf.add(val);
        return " <" + shelf.size() + ">";
    }

    private String retrieve(String text) {
        for (int i = 0; i < shelf.size(); i++) {
            String r = (String) shelf.get(i);
            i++;
            text = str_replace("<" + i + ">", r, text);
        }
        return text;
    }
    */

    private String incomingEntities(String text) {
        return preg_replace("&(?![#a-z0-9]+;)", "x%x%", text);
    }

    private String fixEntities(String text) {
        /*  de-entify any remaining angle brackets or ampersands */
        return str_replace(
        		new String[] { "&gt;", "&lt;", "&amp;" },
        		new String[] { ">", "<", "&" }, 
        		text);
    }

    private String cleanWhiteSpace(String text) {
        String out = preg_replace("(\r\n|\n\r)", "\n", text);
        out = str_replace("\t", "", out);
        out = preg_replace("\n{3,}", "\n\n", out);
        out = preg_replace("\n\\s*\n", "\n\n", out);
        out = preg_replace("\"$", "\" ", out);
        return out;
    }


    private String noTextile(String text) {
    	
    	ReplacementCallback _fTextile = new ReplacementCallback() {
    		public String getReplacement(Matcher matcher) {
    			return fTextile(matcher);
    		};
    	};
    	
        text = preg_replace_callback("(?sm)(^|\\s)<notextile>(.*?)<\\/notextile>(\\s|$)?",
            _fTextile, text);
        
        return preg_replace_callback("(?sm)(^|\\s)==(.*?)==(\\s|$)?",
            _fTextile, text);
    } 

    private String fTextile(Matcher m) {
        String[] modifiers = new String[] {     
            "\"", "&#34;",
            "%", "&#37;",
            "*", "&#42;",
            "+", "&#43;",
            "-", "&#45;",
            "<", "&#60;",
            "=", "&#61;",
            ">", "&#62;",
            "?", "&#63;",     
            "^", "&#94;",
            "_", "&#95;",
            "~", "&#126;"        
        };
        
        String before = group(m, 1);
        String notextile = group(m, 2);
        String after = group(m, 3);
        
        notextile = str_replace(modifiers, notextile);
        return before + "<notextile>" + notextile + "</notextile>" + after;
    }

    /*
    private String footnoteRef(String text) {
        return preg_replace("/\\b\\[([0-9]+)\\](\\s)?/Ue",
            "this.footnoteID(\\'\\1\\',\\'\\2\\')", text);
    }

    private String footnoteID(String id, String t) {
        if (empty(fn(id))) {
            fn(id, uniqid(rand()));
        }
        String fnid = fn(id);
        return "<sup><a href=\"#fn" + fnid + "\">" + id + "</a></sup>" + t;
    }
    */
    
    private String glyphs(String text) {
        text = preg_replace("\"\\z", "\" ", text);
		String pnc = "\\p{Punct}";

        String[] glyph_search = new String[] {
            "([^\\s\\[{(>_*])?\\'(\\s|s\\b|" + pnc + ")",	// single closing
            "'",                                            // single opening
            "([^\\s\\[{(>_*])?\"(?=\\s|" + pnc + ")",		// double closing
            "\"",											// double opening
            "\\b( )?\\.{3,5}",								// ellipsis
            "\\b([A-Z][A-Z0-9]{2,})\\b(?:[(]([^)]*)[)])",	// 3+ uppercase acronym
            "\\s?--\\s?",									// em dash
            "\\s-\\s",										// en dash
            "(\\d+) ?x ?(\\d+)",							// dimension sign
            "(?i)\\b ?[(\\[]TM[\\])]",						// trademark
            "(?i)\\b ?[(\\[]R[\\])]",						// registered
            "(?i)\\b ?[(\\[]C[\\])]"						// copyright
        };                                 

        String[] glyph_replace = new String[] {
        	"$1&#8217;$2",							// single closing
            "&#8216;",								// single opening
            "$1&#8221;",							// double closing
            "&#8220;",								// double opening
            "$1&#8230;",							// ellipsis
            "<acronym title=\"$2\">$1</acronym>",	// 3+ uppercase acronym
            "&#8212;",								// em dash
            " &#8211; ",							// en dash
            "$1&#215;$2",							// dimension sign
            "&#8482;",								// trademark
            "&#174;",								// registered
            "&#169;"								// copyright
        };

        boolean codepre = false;
        
        /*  if no html, do a simple search and replace... */
        if (!preg_match("<.*>", text)) {
            text = preg_replace(glyph_search, glyph_replace, text);
            return text;
        }
        else {
            StringBuffer glyph_out = new StringBuffer();
        	String[] _text = preg_split("(<.*?>)", text, -1, PREG_SPLIT_DELIM_CAPTURE);
            
            for (int i = 0; i < _text.length; i++) {
            	String line = _text[i];
                String offtags = "code|pre|kbd|notextile";

                /*  matches are off if we're between <code>, <pre> etc. */
                if (preg_match("(?i)<(" + offtags + ")>", line)) codepre = true;
                if (preg_match("(?i)<\\/(" + offtags + ")>", line)) codepre = false;

                if (!preg_match("<.*>", line) && !codepre) {
                    line = preg_replace(glyph_search, glyph_replace, line);
                }

                /* do htmlspecial if between <code> */
                if (codepre) {
                    line = htmlspecialchars(line, ENT_NOQUOTES, "UTF-8");
                    line = preg_replace("&lt;(\\/?" + offtags + ")&gt;", "<$1>", line);
                    line = str_replace("&amp;#", "&#", line);
                }

                glyph_out.append(line);
            }
            return glyph_out.toString();
        }
    }

    private String iAlign(String in) {
    	if ("<".equals(in)) {
    		return LEFT;
    	}
    	if ("=".equals(in)) {
    		return CENTER;
    	}
    	if (">".equals(in)) {
    		return RIGHT;
    	}
    	return "";
    }

    private String hAlign(String in) {
        if ("<".equals(in)) {
    		return LEFT;
    	}
    	if ("=".equals(in)) {
    		return CENTER;
    	}
    	if (">".equals(in)) {
    		return RIGHT;
    	}
    	if ("<>".equals(in)) {
    		return JUSTIFY;
    	}
    	return "";
    }

    private String vAlign(String in) {
    	if ("^".equals(in)) {
    		return TOP;
    	}
    	if ("-".equals(in)) {
    		return MIDDLE;
    	}
    	if ("~".equals(in)) {
    		return BOTTOM;
    	}
    	return "";
    }
    
}
