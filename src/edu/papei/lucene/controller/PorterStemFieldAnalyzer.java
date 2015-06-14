package edu.papei.lucene.controller;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.util.Version;

/**
 *	Custom analyzer in order to make use of the Porter stemming algorithm.
 *  LowerCaseTokenizer is used in order for the algorithm to work correctly.
 */
public class PorterStemFieldAnalyzer extends Analyzer {

	@Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
      Tokenizer source = new LowerCaseTokenizer(Version.LUCENE_46, reader);
      return new TokenStreamComponents(source, new PorterStemFilter(source));
    }
  }
