package com.sjl.dsl4xml.performance.parsers;

import com.sjl.dsl4xml.performance.*;

public class DOMPerformanceTest extends PerformanceTestBase {
	
	@Override
	protected String getParserName() {
		return "W3C DOM";
	}

	@Override
	protected TweetsReader newTweetsReader() throws Exception {
		return new DOMTweetsReader();
	}
}
