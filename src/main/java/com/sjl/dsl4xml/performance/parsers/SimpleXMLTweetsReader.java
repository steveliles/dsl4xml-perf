package com.sjl.dsl4xml.performance.parsers;

import java.io.*;
import java.text.*;
import java.util.*;

import org.simpleframework.xml.*;
import org.simpleframework.xml.convert.*;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.core.*;
import org.simpleframework.xml.strategy.*;
import org.simpleframework.xml.stream.*;

import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.performance.model.*;

public class SimpleXMLTweetsReader implements TweetsReader {

	private Serializer serializer;
	
	public SimpleXMLTweetsReader() throws Exception {
		Registry _registry = new Registry();
		Strategy _strategy = new RegistryStrategy(_registry);
		
		_registry.bind(Date.class, new DateConverter());
		
		serializer = new Persister(_strategy);
	}
	
	@Override
	public String getParserName() {
		return "SimpleXML";
	}

	@Override
	public Tweets read(InputStream anInputStream) throws Exception {
		return serializer.read(Tweets.class, anInputStream);
	}
	
	private class DateConverter implements Converter<Date>{
		private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		@Override
		public Date read(InputNode aNode) throws Exception {
			return df.parse(aNode.getValue());
		}

		@Override
		public void write(OutputNode aArg0, Date aArg1) 
		throws Exception {
			// don't care
		}
	}
}
