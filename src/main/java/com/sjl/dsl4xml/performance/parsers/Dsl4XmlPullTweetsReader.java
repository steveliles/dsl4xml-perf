package com.sjl.dsl4xml.performance.parsers;

import static com.sjl.dsl4xml.DocumentReader.*;

import java.io.*;

import com.sjl.dsl4xml.*;
import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.performance.model.*;
import com.sjl.dsl4xml.support.convert.*;

public class Dsl4XmlPullTweetsReader implements TweetsReader {

	private DocumentReader<Tweets> reader;
	
	public Dsl4XmlPullTweetsReader() {
		reader = mappingOf(Tweets.class).to(
			tag("entry", Tweet.class).with(
				tag("published"),
				tag("title"),
				tag("content", Content.class).with(
					attribute("type"),
					pcdataMappedTo("value")
				),
				tag("twitter", "lang").
					withPCDataMappedTo("language"),
				tag("author", Author.class).with(
					tag("name"),
					tag("uri")
				)
			)
		);
		
		reader.registerConverters(new ThreadUnsafeDateConverter("yyyy-MM-dd'T'HH:mm:ss"));
	}
	
	@Override
	public String getParserName() {
		return "DSL4XML (pull)";
	}

	@Override
	public Tweets read(InputStream anInputStream) throws Exception {
		return reader.read(anInputStream, "utf-8");
	}
}
