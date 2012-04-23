package com.sjl.dsl4xml.performance.parsers;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.performance.model.*;

public abstract class AbstractTestForTweetsReaders {

	@Test
	public void testParsesAtomFeedCorrectly() 
	throws Exception {
		TweetsReader _reader = newReader();
		
		Tweets _result = _reader.read(getInputStream());
		Assert.assertEquals(15, _result.size());
		
		Tweet _first = _result.get(0);
		
		Assert.assertEquals(new Date(1333962624000L), _first.getPublished());
		Assert.assertEquals(
			"Note de lecture : Succeeding with Use Cases par Richard" +
			" Denney http://t.co/5lcCXWsO #bookReview #useCases #UML", 
			_first.getTitle()
		);
		
		Assert.assertEquals("html", _first.getContent().getType());
		Assert.assertEquals(
			"Note de lecture : Succeeding with Use Cases par Richard" +
			" Denney <a href=\"http://t.co/5lcCXWsO\">http://t.co/5lcCXWsO</a>" +
			" <a href=\"http://search.twitter.com/search?q=%23bookReview\" " +
			"title=\"#bookReview\" class=\" \">#bookReview</a> " +
			"<a href=\"http://search.twitter.com/search?q=%23useCases\" " +
			"title=\"#useCases\" class=\" \">#useCases</a> <em>" +
			"<a href=\"http://search.twitter.com/search?q=%23UML\" " +
			"title=\"#UML\" class=\" \">#UML</a></em>", 
			_first.getContent().getValue()
		);
		Assert.assertEquals("en", _first.getLanguage());
		
		Assert.assertEquals(
			getExpectedAuthor(), 
			_first.getAuthor()
		);
	}
	
	private InputStream getInputStream() {
		return Tweets.class.getResourceAsStream("twitter-atom.xml");
	}
	
	private Author getExpectedAuthor() {
		Author _a = new Author();
		_a.setName("addinquy (Christophe Addinquy)");
		_a.setUri("http://twitter.com/addinquy");
		return _a;
	}
	
	protected abstract TweetsReader newReader() throws Exception;
	
}
