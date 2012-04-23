package com.sjl.dsl4xml.performance.model;

import java.util.*;

import org.simpleframework.xml.*;

@Root(name="entry", strict=false)
public class Tweet {
	
	@Element 
	private String title;
	
	@Element
	private Date published;
	
	@Element
	private Content content;
	
	@Element(name="lang")
	private String language;
	
	@Element
	private Author author;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String aTitle) {
		title = aTitle;
	}
	
	public Date getPublished() {
		return published;
	}
	
	public void setPublished(Date aDate) {
		published = aDate;
	}
	
	public Content getContent() {
		return content;
	}
	
	public void setContent(Content aContent) {
		content = aContent;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String aLanguage) {
		language = aLanguage;
	}
	
	public Author getAuthor() {
		return author;
	}
	
	public void setAuthor(Author aAuthor) {
		author = aAuthor;
	}
}
