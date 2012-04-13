package com.sjl.dsl4xml.performance.parsers;

import com.sjl.dsl4xml.performance.*;

public class PullParserPerformanceTest extends PerformanceTestBase {
	
	@Override
	protected String getParserName() {
		return "Pull-Parser";
	}

	@Override
	protected TweetsReader newTweetsReader() throws Exception {
		return new PullParserTweetsReader();
	}

}
