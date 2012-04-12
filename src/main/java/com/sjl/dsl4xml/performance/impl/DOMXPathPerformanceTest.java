package com.sjl.dsl4xml.performance.impl;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;

import com.sjl.dsl4xml.performance.*;

public class DOMXPathPerformanceTest extends PerformanceTest {

	public DOMXPathPerformanceTest(int aMaxConcurrency, int anIterationsPerThread) {
		super(aMaxConcurrency, anIterationsPerThread);
	}
	
	public DOMXPathPerformanceTest() {
		super();
	}
	
	@Override
	protected String getParserName() {
		return "W3C DOM XPath";
	}

	@Override
	protected ReadingThread newReadingThread(CyclicBarrier aGate) throws Exception {
		final DocumentBuilder _builder = 
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
				
		XPathFactory _f = XPathFactory.newInstance();
		
		final XPathExpression _entry = _f.newXPath().compile("/feed/entry");
		final XPathExpression _published = _f.newXPath().compile(".//published");
		final XPathExpression _title = _f.newXPath().compile(".//title");
		final XPathExpression _contentType = _f.newXPath().compile(".//content/@type");
		final XPathExpression _content = _f.newXPath().compile(".//content");
		final XPathExpression _lang = _f.newXPath().compile(".//twitter:lang");
		final XPathExpression _authorName = _f.newXPath().compile(".//author/name");
		final XPathExpression _authorUri = _f.newXPath().compile(".//author/uri");
		
		final DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		return new ReadingThread(aGate) {
			@Override
			public Tweets read(InputStream anInputStream) {
				try {
					Tweets _result = new Tweets();
					Document _document = _builder.parse(anInputStream);
	
					NodeList _entries = (NodeList) _entry.evaluate(_document, XPathConstants.NODESET);					
					for (int i=0; i<_entries.getLength(); i++) {
						Tweet _tweet = new Tweet();
						_result.addTweet(_tweet);
						
						Node _entryNode = _entries.item(i);
						
						_tweet.setPublished(getPublishedDate(_entryNode));
						_tweet.setTitle(_title.evaluate(_entryNode));
						_tweet.setLanguage(_lang.evaluate(_entryNode));
						
						Content _c = new Content();
						_tweet.setContent(_c);
						
						_c.setType(_contentType.evaluate(_entryNode));
						_c.setValue(_content.evaluate(_entryNode));
						
						Author _a = new Author();
						_tweet.setAuthor(_a);
						
						_a.setName(_authorName.evaluate(_entryNode));
						_a.setUri(_authorUri.evaluate(_entryNode));
					}
					
					return _result;
				} catch (Exception anExc) {
					throw new RuntimeException(anExc);
				}
			}
			
			private Date getPublishedDate(Node aNode) 
			throws Exception {
				return _dateFormat.parse(_published.evaluate(aNode));
			}
		};
	}
	
}
