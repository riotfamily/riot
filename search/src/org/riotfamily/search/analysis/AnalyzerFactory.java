package org.riotfamily.search.analysis;

import org.apache.lucene.analysis.Analyzer;

public interface AnalyzerFactory {

	public Analyzer getAnalyzer(String language);

}
