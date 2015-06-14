package edu.papei.lucene.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class CacmDocument {

	private List<String> authors;
	private int id;
	private List<String> synopsis;
	private List<String> title;

	public CacmDocument() {
		this.authors = new ArrayList<String>();
		this.id = -1;
		this.synopsis = new ArrayList<String>();
		this.title = new ArrayList<String>();
	}
	
	public void addAuthor(String author) {
		this.authors.add(author);
	}
	
	public String getAuthors() {
		return StringUtils.join(this.authors, ",");
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSynopsis() {
		return StringUtils.join(this.synopsis, ",");
	}

	public void setSynopsis(List<String> synopsis) {
		this.synopsis = synopsis;
	}

	public void addSynopsis(String s) {
		this.synopsis.add(s);
	}

	public String getTitle() {
		return StringUtils.join(this.title, ",");
	}

	public void setTitle(List<String> title) {
		this.title = title;
	}

	public void addTitle(String s) {
		this.title.add(s);
	}
	
}
