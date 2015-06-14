package edu.papei.lucene.constant;

/**
 *	Class containing static fields
 */
public class Globals {

	public static final String STOPWORDS = "input/cacm/common_words";
	// folder name to store the index
	public static final String indexPath = "index";
	public static final int MAX_RESULTS = 10000;
	
	public static class Fields {
		public static final char PREFIX = '.';
		public static final char AUTHORS = 'A';
		public static final char ID = 'I';
		public static final char TITLE = 'T';
		public static final char SYNOPSIS = 'W';
	}
}
