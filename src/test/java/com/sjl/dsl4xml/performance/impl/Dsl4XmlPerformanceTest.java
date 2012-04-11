package com.sjl.dsl4xml.performance.impl;

import static com.sjl.dsl4xml.DocumentReader.*;

import java.io.*;
import java.util.concurrent.*;

import com.sjl.dsl4xml.*;
import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.support.convert.*;

public class Dsl4XmlPerformanceTest extends PerformanceTest {

	public Dsl4XmlPerformanceTest(int aMaxConcurrency, int anIterationsPerThread) {
		super(aMaxConcurrency, anIterationsPerThread);
	}
	
	public Dsl4XmlPerformanceTest() {
		super();
	}
	
	@Override
	protected String getParserName() {
		return "dsl4xml (pull-parsing)";
	}

	@Override
	protected ReadingThread newReadingThread(CyclicBarrier aGate) {
		final DocumentReader<Tweets> _r = mappingOf(Tweets.class).to(
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
		
		_r.registerConverters(new ThreadUnsafeDateConverter("yyyy-MM-dd'T'HH:mm:ss"));
			
		return new ReadingThread(aGate) {
			@Override
			public Tweets read(InputStream anInputStream) {
				return _r.read(anInputStream, "utf-8");
			}
		};
	}
	
}
