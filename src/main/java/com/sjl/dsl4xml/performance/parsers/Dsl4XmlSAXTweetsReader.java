package com.sjl.dsl4xml.performance.parsers;

import static com.sjl.dsl4xml.SAXDocumentReader.*;

import java.io.*;

import com.sjl.dsl4xml.*;
import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.performance.model.*;
import com.sjl.dsl4xml.support.convert.*;

public class Dsl4XmlSAXTweetsReader implements TweetsReader {

	private SAXDocumentReader<Tweets> reader;
	
	public Dsl4XmlSAXTweetsReader() {
		reader = mappingOf("feed", Tweets.class).to(
			tag("entry", Tweet.class).with(
				tag("published"),
				tag("title"),
				tag("content", Content.class).with(
					attributes("type")
				).withPCDataMappedTo("value"),
				tag("twitter:lang").
					withPCDataMappedTo("language"),
				tag("author", Author.class).with(
					tag("name"),
					tag("uri")
				)
			)
		);
			
		reader.registerConverters(new ThreadSafeDateConverter("yyyy-MM-dd'T'HH:mm:ss"));
	}
	
	@Override
	public String getParserName() {
		return "DSL4XML (SAX)";
	}

	@Override
	public Tweets read(InputStream anInputStream) throws Exception {
		return reader.read(anInputStream, "utf-8");
	}
}
