package com.sjl.dsl4xml.performance.parsers;

import java.io.*;
import java.text.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.ext.*;
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
	    reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler); 
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

	private static class TweetsHandler 
	extends DefaultLexicalHandler {

		private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		private Tweets tweets;
		private Tweet tweet;
		private Content content;
		private Author author;
		private String currentElement;
		private StringBuilder chars;
		
		public Tweets getResult() {
			return tweets;
		}
		
		@Override
		public void startDocument() throws SAXException {
			chars = new StringBuilder();
			tweets = new Tweets();
		}

		@Override
		public void startElement(
			String aUri, String aLocalName, 
		    String aQName, Attributes aAttributes
		) throws SAXException {
			currentElement = aQName;
			chars.setLength(0);
			
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
			if (chars.length() > 0) {
				setCharacterValue(chars);
			}
			
			currentElement = null;
		}
		
		@Override
		public void startEntity(String aName) 
		throws SAXException {		
		}
		
		@Override
		public void endEntity(String aName) 
		throws SAXException {
		}

		@Override
		public void characters(char[] aCh, int aStart, int aLength)
		throws SAXException {			
			chars.append(aCh, aStart, aLength);			
		}
		
		private void setCharacterValue(StringBuilder aCharacters) 
		throws SAXException {
			if ("published".equals(currentElement)) {
				try {
					tweet.setPublished(dateFormat.parse(aCharacters.toString()));
				} catch (ParseException anExc) {
					throw new SAXException(anExc);
				}
			} else if (("title".equals(currentElement)) && (tweet != null)) {
				tweet.setTitle(aCharacters.toString());
			} else if ("content".equals(currentElement)) {
				content.setValue(aCharacters.toString());
			} else if ("twitter:lang".equals(currentElement)) {
				tweet.setLanguage(aCharacters.toString());
			} else if ("name".equals(currentElement)) {
				author.setName(aCharacters.toString());
			} else if ("uri".equals(currentElement)) {
				author.setUri(aCharacters.toString());
			}
		}
	}
	
	static class DefaultLexicalHandler 
	extends DefaultHandler 
	implements LexicalHandler {

		@Override
		public void comment(char[] aArg0, int aArg1, int aArg2)
		throws SAXException {}

		@Override
		public void endCDATA() 
		throws SAXException {}

		@Override
		public void endDTD() 
		throws SAXException {}

		@Override
		public void endEntity(String aName) 
		throws SAXException {}

		@Override
		public void startCDATA() 
		throws SAXException {}

		@Override
		public void startDTD(String aArg0, String aArg1, String aArg2)
	    throws SAXException {}

		@Override
		public void startEntity(String aName) 
		throws SAXException {}
	}
}
