package com.sjl.dsl4xml.performance.dsl4xml;

import static com.sjl.dsl4xml.DocumentReader.*;

import java.io.*;
import java.util.concurrent.*;

import com.sjl.dsl4xml.*;
import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.support.convert.*;

public class Dsl4XmlPerformanceTest extends PerformanceTest {

	private DocumentReader<Tweets> tweetsReader;
	
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

	public void oneTimeParserSetup() throws IOException {
		tweetsReader = mappingOf(Tweets.class).to(
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
		
		tweetsReader.registerConverters(new ThreadSafeDateConverter("yyyy-MM-dd'T'HH:mm:ss"));
	}

	@Override
	protected ReadingThread newReadingThread(CyclicBarrier aGate) {
		return new ReadingThread(aGate) {
			@Override
			public Tweets read(InputStream anInputStream) {
				return tweetsReader.read(anInputStream, "utf-8");
			}
		};
	}
	
}
