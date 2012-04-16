package com.sjl.dsl4xml.performance.parsers;

import com.sjl.dsl4xml.performance.*;

public class SimpleXMLPerformanceTest extends PerformanceTestBase {

	@Override
	protected String getParserName() {
		return "SimpleXML";
	}

	@Override
	protected TweetsReader newTweetsReader() throws Exception {
		return new SimpleXMLReader();
	}
	
}
