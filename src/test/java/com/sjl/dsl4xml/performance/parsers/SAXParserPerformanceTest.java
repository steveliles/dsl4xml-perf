package com.sjl.dsl4xml.performance.parsers;

import com.sjl.dsl4xml.performance.*;

public class SAXParserPerformanceTest extends PerformanceTestBase {

	@Override
	protected String getParserName() {
		return "SAX Parser";
	}

	@Override
	protected TweetsReader newTweetsReader()
	throws Exception {
		return new SAXTweetsReader();
	}
	
}
