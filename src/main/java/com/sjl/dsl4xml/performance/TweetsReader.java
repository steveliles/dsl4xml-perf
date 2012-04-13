package com.sjl.dsl4xml.performance;

import java.io.*;

import com.sjl.dsl4xml.performance.model.*;

public interface TweetsReader {

	public String getParserName();
	
	public Tweets read(InputStream anInputStream)
	throws Exception;
	
}
