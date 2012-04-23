package com.sjl.dsl4xml.performance.parsers;

import com.sjl.dsl4xml.performance.*;

public class Dsl4XmlSAXPerformanceTest extends PerformanceTestBase {
	
	@Override
	protected String getParserName() {
		return "dsl4xml (sax-parsing)";
	}

	@Override
	protected TweetsReader newTweetsReader() throws Exception {
		return new Dsl4XmlSAXTweetsReader();
	}
	
}
