package com.sjl.dsl4xml.performance.parsers;

import com.sjl.dsl4xml.performance.*;

public class DOMParserTweetsReaderTest extends AbstractTestForTweetsReaders {

	@Override
	protected TweetsReader newReader() throws Exception {
		return new DOMTweetsReader();
	}

}
