package com.sjl.dsl4xml.performance.parsers;

import com.sjl.dsl4xml.performance.*;

public class Dsl4XmlPerformanceTest extends PerformanceTestBase {
	
	@Override
	protected String getParserName() {
		return "dsl4xml (pull-parsing)";
	}

	@Override
	protected TweetsReader newTweetsReader() throws Exception {
		return new Dsl4XmlTweetsReader();
	}
	
}
