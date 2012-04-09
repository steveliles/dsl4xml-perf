package com.sjl.dsl4xml.performance.dsl4xml;

import static com.sjl.dsl4xml.DocumentReader.*;

import java.io.*;
import java.util.concurrent.*;

import org.junit.*;

import com.sjl.dsl4xml.*;
import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.support.convert.*;

public class Dsl4XmlPerformanceTest extends PerformanceTest {

	private DocumentReader<Tweets> tweetsReader;
	
	@Before
	public void createReusableReader() throws IOException {
		tweetsReader = mappingOf(Tweets.class).to(
			tag("entry", Tweet.class).with(
				tag("published"),
				tag("title"),
				tag("content", Content.class).with(
					attribute("type"),
					pcdataMappedTo("value")
				),
				tag("twitter:lang").withPCDataMappedTo("language"),
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
