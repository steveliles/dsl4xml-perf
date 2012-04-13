package com.sjl.dsl4xml.performance.parsers;

import java.io.*;
import java.text.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.performance.model.*;

public class SAXTweetsReader implements TweetsReader {

	private XMLReader reader;
	private TweetsHandler handler;
	
	public SAXTweetsReader() 
	throws Exception {
		SAXParserFactory _f = SAXParserFactory.newInstance();
	    SAXParser _p = _f.newSAXParser();
	    reader = _p.getXMLReader();
	    handler = new TweetsHandler();
	    reader.setContentHandler(handler);
	}

	@Override
	public String getParserName() {
		return "SAX";
	}

	@Override
	public Tweets read(InputStream anInputStream) throws Exception {
		reader.parse(new InputSource(anInputStream));
		return handler.getResult();
	}

	private static class TweetsHandler extends DefaultHandler {

		private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		private Tweets tweets;
		private Tweet tweet;
		private Content content;
		private Author author;
		private String currentElement;
		
		public Tweets getResult() {
			return tweets;
		}
		
		@Override
		public void startDocument() throws SAXException {
			tweets = new Tweets();
		}

		@Override
		public void startElement(
			String aUri, String aLocalName, 
		    String aQName, Attributes aAttributes
		) throws SAXException {
			currentElement = aQName;
			if ("entry".equals(aQName)) {
				tweets.addTweet(tweet = new Tweet());
			} else if ("content".equals(aQName)) {
				tweet.setContent(content = new Content());
				content.setType(aAttributes.getValue("type"));
			} else if ("author".equals(aQName)) {
				tweet.setAuthor(author = new Author());
			}
		}

		@Override
		public void endElement(String aUri, String aLocalName, String aQName) 
		throws SAXException {
			currentElement = null;
		}

		@Override
		public void characters(char[] aCh, int aStart, int aLength)
		throws SAXException {
			if ("published".equals(currentElement)) {
				try {
					tweet.setPublished(dateFormat.parse(new String(aCh, aStart, aLength)));
				} catch (ParseException anExc) {
					throw new SAXException(anExc);
				}
			} else if (("title".equals(currentElement)) && (tweet != null)) {
				tweet.setTitle(new String(aCh, aStart, aLength));
			} else if ("content".equals(currentElement)) {
				content.setValue(new String(aCh, aStart, aLength));
			} else if ("lang".equals(currentElement)) {
				tweet.setLanguage(new String(aCh, aStart, aLength));
			} else if ("name".equals(currentElement)) {
				author.setName(new String(aCh, aStart, aLength));
			} else if ("uri".equals(currentElement)) {
				author.setUri(new String(aCh, aStart, aLength));
			}
		}
	}
}
