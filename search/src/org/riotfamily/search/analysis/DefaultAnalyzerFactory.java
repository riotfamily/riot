package org.riotfamily.search.analysis;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.riotfamily.common.util.ResourceUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

public class DefaultAnalyzerFactory implements AnalyzerFactory {

	private static Map snowballNames = new HashMap();
	static {
		snowballNames.put("da", "Danish");
		snowballNames.put("nl", "Dutch");
		snowballNames.put("en", "English");
		snowballNames.put("fi", "Finnish");
		snowballNames.put("fr", "French");
		snowballNames.put("de", "German");
		snowballNames.put("it", "Italian");
		snowballNames.put("no", "Norwegian");
		snowballNames.put("pt", "Portuguese");
		snowballNames.put("es", "Spanish");
		snowballNames.put("se", "Swedish");
	}

	private Analyzer defaultAnalyzer = new StandardAnalyzer();
	
	public Analyzer getAnalyzer(String language) {
		if (language != null) {
			String snowballName = (String) snowballNames.get(language);
			if (snowballName != null) {
				String[] stopWords = getStopWords(language);
				if (stopWords != null) {
					return new SnowballAnalyzer(snowballName, stopWords);
				}
				return new SnowballAnalyzer(snowballName);
			}
			if (language.equals("ja") || language.equals("ko") || language.endsWith("zh")) {
				return new CJKAnalyzer();
			}
			if (language.equals("th")) {
				return new ThaiAnalyzer();
			}
			if (language.equals("el")) {
				return new GreekAnalyzer();
			}
			if (language.equals("cs")) {
				return new CzechAnalyzer();
			}
		}
		return defaultAnalyzer;
	}
	
	protected String[] getStopWords(String language) {
		try {
			String path = ResourceUtils.getPath(this, "/stopwords/" + language + ".txt");
			Resource res = new ClassPathResource(path);
			if (res.exists()) {
				HashSet wordSet = WordlistLoader.getWordSet(
						new InputStreamReader(res.getInputStream(), "UTF-8"));
				
				return StringUtils.toStringArray(wordSet);
			}
		}
		catch (IOException e) {
		}
		return null;
	}
}
