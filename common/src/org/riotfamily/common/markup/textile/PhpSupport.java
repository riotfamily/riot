package org.riotfamily.common.markup.textile;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

public class PhpSupport {

	/**
     * Replace all occurrences of the search string with the replacement string.
     */
    protected static String str_replace(String search, String replace, String subject) {
        if (subject == null || "".equals(subject)) {
            return subject;
        }

        if (replace == null) {
            return subject;
        }

        if ("".equals(search)) {
            return subject;
        }

        int s = 0;
        int e = 0;
        
        StringBuffer result = new StringBuffer();
        while ((e = subject.indexOf(search, s)) >= 0) {
            result.append(subject.substring(s, e));
            result.append(replace);
            s = e + search.length();
        }
        result.append(subject.substring(s));
        return result.toString();
    }
    
    protected static String str_replace(String[] search, 
    		String[] replace, String text) {
    	
    	if (search.length != replace.length) {
    		throw new IllegalArgumentException("The number of search strings " 
    				+ "must match the number of replacements.");
    	}
    	
    	for (int i = 0; i < search.length; i++) {
    		text = preg_replace(search[i], replace[i], text);
    	}
    	
    	return text;
    }
    
    protected static String str_replace(String[] searchAndReplace, 
    		String text) {
    	
    	if (searchAndReplace.length % 2 != 0) {
    		throw new IllegalArgumentException("The array must contain an " +
    				"even number of entries.");
    	}
    	
    	for (int i = 0; i < searchAndReplace.length - 1; i += 2) {
    		text = str_replace(searchAndReplace[i], 
    				searchAndReplace[i + 1], text);
    	}
    	
    	return text;
    }
    
    protected static String preg_replace(String pattern, String replacement, 
    		String text) {
    	
    	Matcher matcher = Pattern.compile(pattern).matcher(text);
    	return matcher.replaceAll(replacement);
    }
    
    
    protected static String preg_replace(String[] patterns, 
    		String[] replacements, String text) {
    	
    	if (patterns.length != replacements.length) {
    		throw new IllegalArgumentException("The number of patterns must " +
    				"match the number of replacements.");
    	}
    	
    	for (int i = 0; i < patterns.length; i++) {
    		text = preg_replace(patterns[i], replacements[i], text);
    	}
    	
    	return text;
    }
    
    protected static String preg_replace_callback(String pattern, 
    		ReplacementCallback callback, String text) {
    	
    	Pattern p = Pattern.compile(pattern);
    	Matcher m = p.matcher(text);
    	StringBuffer sb = new StringBuffer();
    	while (m.find()) {
    	    m.appendReplacement(sb, callback.getReplacement(m));
    	}
    	m.appendTail(sb);
    	return sb.toString();
    }
    
    protected static boolean preg_match(String pattern, String text) {
    	return preg_match(pattern, text, null);
    }
    
    protected static boolean preg_match(String pattern, String text, 
    		MatchResult matches) {
    	
    	if (text == null) {
    		return false;
    	}
    	Pattern p = Pattern.compile(pattern);
    	Matcher m = p.matcher(text);
    	if (m.find()) {
    		if (matches != null) {
    			matches.init(m);
    		}
    		return true;
    	}
    	return false;
    }
    
    protected static final int PREG_SPLIT_NO_EMPTY = 1;
    
    protected static final int PREG_SPLIT_DELIM_CAPTURE = 2;
    
    /**
     * Returns an array containing substrings of subject split along 
     * boundaries matched by pattern.
     */
    protected static String[] preg_split(String pattern, String subject, 
    		int limit, int flags) {
    	
    	int index = 0;
        boolean matchLimited = limit > 0;
        
        boolean addDelimiters = (flags & PREG_SPLIT_DELIM_CAPTURE) != 0;
        boolean includeEmpty = (flags & PREG_SPLIT_NO_EMPTY) == 0;
        
        ArrayList matchList = new ArrayList();
        
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(subject);

        while(m.find()) {
            if (!matchLimited || matchList.size() < limit) {
                String match = subject.substring(index, m.start());
                if (includeEmpty || StringUtils.hasLength(match)) {
	                matchList.add(match);
	                if (addDelimiters) {
	                	matchList.add(m.group());
	                }
                }
                index = m.end();
            }
            else {
            	break;
            }
        }

        // If no match was found, return this
        if (index == 0) {
            return new String[] { subject };
        }

        // Add remaining segment
        if (!matchLimited || matchList.size() < limit) {
            matchList.add(subject.substring(index, subject.length()));
        }
        
        int resultSize = matchList.size();
        String[] result = new String[resultSize];
        return (String[]) matchList.subList(0, resultSize).toArray(result);
    }

    protected static String[] explode(String separator, String text) {
    	return StringUtils.delimitedListToStringArray(text, separator);
    }
    
    protected static final int ENT_NOQUOTES = 1;
    
    protected static String htmlspecialchars(String s, int flags, String encoding) {
    	//TODO Check if it's really okay to ignore flags and encoding
    	return HtmlUtils.htmlEscape(s);
    }
    
    protected static String htmlspecialchars(String s) {
    	return HtmlUtils.htmlEscape(s);
    }
    
    protected interface ReplacementCallback {
    	public String getReplacement(Matcher matcher);
    }
    
    protected class MatchResult {
    	
    	private String[] matches;
    	
    	public void init(Matcher matcher) {
    		int groups = matcher.groupCount() + 1;
    		matches = new String[groups];
    		for (int i = 0; i < groups; i++) {
    			matches[i] = matcher.group(i);
    		}
    	}
    	
    	public String group(int i) {
    		if (matches != null && matches.length > i) {
    			return matches[i];
    		}
    		return null;
    	}
    }
    
    protected static String group(Matcher m, int group) {
    	String s = m.group(group);
    	return s != null ? s : "";
    }

}
