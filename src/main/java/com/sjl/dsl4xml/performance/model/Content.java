package com.sjl.dsl4xml.performance.model;

import org.simpleframework.xml.*;

public class Content {
	
	@Attribute
	public String type;
	
	@Text
	public String value;
	
	public String getType() {
		return type;
	}
	
	public void setType(String aType) {
		type = aType;			
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String aValue) {
		value = aValue;		
	}
}