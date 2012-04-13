package com.sjl.dsl4xml.performance;

import org.junit.*;

import com.sjl.dsl4xml.performance.PerformanceTestRunner.TweetsReaderFactory;
import com.sjl.dsl4xml.performance.model.*;

public abstract class PerformanceTestBase {

	private static final int MAX_CONC = 8;
	private static final int ITERATIONS = 100;
	
	private byte[] xml;

	protected abstract String getParserName();
	
	protected abstract TweetsReader newTweetsReader()
	throws Exception;
	
	@Before
	public void prepareXml() throws Exception {
		xml = Streams.readInputIntoByteArray(Tweets.class.getResourceAsStream("twitter-atom.xml"));
	}
	
	@Test
	public void testMultithreadedParsing() throws Exception {
		PerformanceTestRunner _ppt = new PerformanceTestRunner(
			new TweetsReaderFactory() {
				@Override
				public String getParserType() {
					return getParserName();
				}

				@Override
				public TweetsReader newReader() throws Exception {
					return newTweetsReader();
				}	
			}
		);
		Statistics _stats = _ppt.collectStatistics(xml, MAX_CONC, ITERATIONS);
		System.out.println(_stats);
	}
	
}
