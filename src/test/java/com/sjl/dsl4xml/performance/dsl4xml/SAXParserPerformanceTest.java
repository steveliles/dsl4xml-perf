package com.sjl.dsl4xml.performance.dsl4xml;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.sjl.dsl4xml.performance.*;

public class SAXParserPerformanceTest extends PerformanceTest {

	public SAXParserPerformanceTest(int aMaxConcurrency, int anIterationsPerThread) {
		super(aMaxConcurrency, anIterationsPerThread);
	}
	
	public SAXParserPerformanceTest() {
		super();
	}
	
	@Override
	protected String getParserName() {
		return "SAX Parser";
	}
	
	@Override
	protected ReadingThread newReadingThread(CyclicBarrier aGate) throws Exception {
		SAXParserFactory _f = SAXParserFactory.newInstance();
        SAXParser _p = _f.newSAXParser();
        final XMLReader _r = _p.getXMLReader();
        final TweetsHandler _h = new TweetsHandler();
        _r.setContentHandler(_h);
		
		return new ReadingThread(aGate) {
			public Tweets read(InputStream anInputStream) {
				try {
					_r.parse(new InputSource(anInputStream));
					return _h.getResult();
				} catch (Exception anExc) {
					throw new RuntimeException(anExc);
				}
			}
		};
	}
	
	class TweetsHandler extends DefaultHandler {

		private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		private Tweets tweets;
		private Tweet tweet;
		private Stack<String> ctx;
		
		public Tweets getResult() {
			return tweets;
		}
		
		@Override
		public void startDocument() throws SAXException {
			tweets = new Tweets();
			ctx = new Stack<String>();
		}

		@Override
		public void startElement(
			String aUri, String aLocalName, 
		    String aQName, Attributes aAttributes
		) throws SAXException {	
			ctx.push(aQName);
			if ("entry".equals(aQName)) {
				tweets.addTweet(tweet = new Tweet());
			} else if ("content".equals(aQName)) {
				tweet.setContent(new Content());
				tweet.getContent().setType(aAttributes.getValue("type"));
			} else if ("author".equals(aQName)) {
				tweet.setAuthor(new Author());
			}
		}

		@Override
		public void endElement(String aUri, String aLocalName, String aQName) 
		throws SAXException {
			ctx.pop();
		}

		@Override
		public void characters(char[] aCh, int aStart, int aLength)
		throws SAXException {
			String _currentElement = ctx.peek();
			
			if ("published".equals(_currentElement)) {
				try {
					tweet.setPublished(dateFormat.parse(new String(aCh, aStart, aLength)));
				} catch (ParseException anExc) {
					throw new SAXException(anExc);
				}
			} else if (("title".equals(_currentElement)) && (tweet != null)) {
				tweet.setTitle(new String(aCh, aStart, aLength));
			} else if ("content".equals(_currentElement)) {
				tweet.getContent().setValue(new String(aCh, aStart, aLength));
			} else if ("lang".equals(_currentElement)) {
				tweet.setLanguage(new String(aCh, aStart, aLength));
			} else if ("name".equals(_currentElement)) {
				tweet.getAuthor().setName(new String(aCh, aStart, aLength));
			} else if ("uri".equals(_currentElement)) {
				tweet.getAuthor().setUri(new String(aCh, aStart, aLength));
			}
		}
	}
}
