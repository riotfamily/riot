package org.riotfamily.website.txt2img;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.font.TextMeasurer;
import java.text.AttributedString;

import org.riotfamily.common.collection.FlatMap;

public class HyphenatedLineBreakMeasurerer {

	private StringBuffer sb;
	
	private FlatMap attrs;
	
	private FontRenderContext fc;
	
    private TextMeasurer measurer;
    
    private int start;
    
    private int end;
    
    private int last;
    
    private int hyphenWidth;
    
    public HyphenatedLineBreakMeasurerer(String text, Font font, Color color,
    		FontRenderContext fc) {
    
    	this.sb = new StringBuffer(text.replace('\u00AD', '\t'));
    	this.attrs = new FlatMap();
    	this.attrs.put(TextAttribute.FOREGROUND, color);
    	this.attrs.put(TextAttribute.FONT, font);
    	this.fc = fc;
    	createMeasurer();
    	hyphenWidth = (int) font.getStringBounds("-", fc).getWidth();
    }

    private void createMeasurer() {
    	AttributedString as = new AttributedString(sb.toString(), attrs);
    	this.measurer = new TextMeasurer(as.getIterator(), fc);
    	last = sb.length() - 1;
    }
    
    private void insertHyphen() {
    	sb.insert(end, '-');
    	end++;
    	createMeasurer();
    }
    
    private boolean isWhitespace(int i) {
    	char c = sb.charAt(i);
    	return Character.isWhitespace(c) && c != '\t';
    }
    
    private boolean isWhitespaceOrHyphen(int i) {
    	char c = sb.charAt(i);
    	return c == '-' || (Character.isWhitespace(c) && c != '\t');
    }
    
    private int lastWhitespace() {
    	for (int i = end - 1; i > start; i--) {
    		if (isWhitespaceOrHyphen(i)) {
    			return i;
    		}
    	}
    	return -1;
    }
    
    private int getNextBreak() {
    	int i = sb.indexOf("\n", start);
    	if (i < end) {
    		return i;
    	}
    	return -1;
    }
    
    private boolean isBreakAtWhitespace() {
    	return end > last || isWhitespaceOrHyphen(end - 1) || isWhitespace(end);
    }
    
    public boolean hasNext() {
    	start = end;
    	while (start <= last && isWhitespace(start)) {
    		start++;
    	}
    	return start <= last;
    }
    
    
    public TextLayout nextLayout(float wrappingWidth) {
    	if (!hasNext()) {
    		return null;
    	}
    	end = measurer.getLineBreakIndex(start, wrappingWidth);
    	int nextBreak = getNextBreak();
    	if (nextBreak != -1) {
    		end = nextBreak;
    	}
    	else {
			if (!isBreakAtWhitespace()) {
				int i = lastWhitespace();
	    		if (i != -1) {
	    			end = i + 1;
	    		}
	    		else {
	    			breakAtSoftHyphen(wrappingWidth);
	    		}
			}
    	}
    	return measurer.getLayout(start, end);
    }
    
    private void breakAtSoftHyphen(float wrappingWidth) {
    	for (int i = end - 1; i > start; i--) {
    		if (sb.charAt(i) == '\t') {
    			float w = measurer.getAdvanceBetween(start, i) + hyphenWidth;
    			if (w <= wrappingWidth) {
    				end = i;
    				insertHyphen();
    			}
    		}
    	}
    }

}
