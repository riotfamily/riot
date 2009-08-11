package org.riotfamily.components.meta;

import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.collection.EnumerationIterator;
import org.riotfamily.common.util.Generics;

import freemarker.core.Comment;
import freemarker.core.TemplateElement;
import freemarker.template.Template;

public class FreeMarkerMetaDataExtractor {

	/* Pattern to strip leading dashes from a comment line */
	private static final Pattern LINE_START = 
			Pattern.compile("^\\s*-*\\s?", Pattern.MULTILINE);

	/* Pattern to extract meta-tags like @icon */
	private static final Pattern DOC_TAG = 
			Pattern.compile("(?:^|\\n)\\s*@(\\w+)\\s+([\\w\\W]+?)?\\s*(?=\\n@|$)");
		
	@SuppressWarnings("unchecked")
	public static Map<String, String> extractMetaData(Template template) {
		Enumeration children = template.getRootTreeNode().children();
		for (TemplateElement child : new EnumerationIterator<TemplateElement>(children)) {
			if (child instanceof Comment) {
				Comment c = (Comment) child;
				return extractTags(c.getText());
			}
		}
		return null;
	}
	
	private static Map<String, String> extractTags(String comment) {
		Map<String, String> tags = Generics.newHashMap();
		if (comment != null) {
			comment = LINE_START.matcher(comment).replaceAll("");
			Matcher m = DOC_TAG.matcher(comment);
			while (m.find()) {
				String s = m.group(2);
				if (s != null) {
					s = s.replace('\n', ' ');
				}
				else {
					s = "";
				}
				tags.put(m.group(1), s);
			}
		}
		return tags;
	}
	
}
