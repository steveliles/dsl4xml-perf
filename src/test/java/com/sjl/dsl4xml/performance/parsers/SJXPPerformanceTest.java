package com.sjl.dsl4xml.performance.parsers;

import com.sjl.dsl4xml.performance.*;

public class SJXPPerformanceTest extends PerformanceTestBase {

	@Override
	protected String getParserName() {
		return "SJXP (pull-parsing)";
	}

	@Override
	protected TweetsReader newTweetsReader() {
		return new SJXPTweetsReader();
	}
	
}
