package com.sjl.dsl4xml.performance.model;

import org.simpleframework.xml.*;

public class Author {
	
	@Element
	private String name;
	
	@Element
	private String uri;
	
	public Author() {}
	
	public String getName() {
		return name;
	}
	
	public void setName(String aName) {
		name = aName;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String aUri) {
		uri = aUri;
	}
}
