package com.sjl.dsl4xml.performance.parsers;

import java.io.*;

import org.simpleframework.xml.*;
import org.simpleframework.xml.core.*;

import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.performance.model.*;

public class SimpleXMLReader implements TweetsReader {

	private Serializer serializer;
	
	public SimpleXMLReader() {
		serializer = new Persister();
	}
	
	@Override
	public String getParserName() {
		return "SimpleXML";
	}

	@Override
	public Tweets read(InputStream anInputStream) throws Exception {
		return serializer.read(Tweets.class, anInputStream);
	}
	
}
