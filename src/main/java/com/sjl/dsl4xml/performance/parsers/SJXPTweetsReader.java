package com.sjl.dsl4xml.performance.parsers;

import java.io.*;
import java.text.*;

import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.performance.model.*;
import com.thebuzzmedia.sjxp.*;
import com.thebuzzmedia.sjxp.rule.*;
import com.thebuzzmedia.sjxp.rule.IRule.*;

public class SJXPTweetsReader implements TweetsReader {

	private Tweet currentTweet;
	private DateFormat dateFormat;
	private XMLParser<Tweets> parser; 
	
	private IRule<Tweets> tweet = new DefaultRule<Tweets>(Type.TAG, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry") {
		public void handleTag(XMLParser<Tweets> aParser, boolean aIsStartTag, Tweets aUserObject) {
			if (aIsStartTag)
				aUserObject.addTweet(currentTweet = new Tweet());
		}	
	};
	
	private IRule<Tweets> published = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]published") {
		public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {
			try {					
				currentTweet.setPublished(dateFormat.parse(aText));
			} catch (ParseException anExc) {
				throw new XMLParserException("date-parsing problem", anExc);
			}
		}			
	}; 
	
	private IRule<Tweets> title = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]title") {
		public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {
			currentTweet.setTitle(aText);
		}			
	};
	
	IRule<Tweets> content = new DefaultRule<Tweets>(Type.TAG, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]content") {
		public void handleTag(XMLParser<Tweets> aParser, boolean aIsStartTag, Tweets aUserObject) {
			if (aIsStartTag)
				currentTweet.setContent(new Content());
			super.handleTag(aParser, aIsStartTag, aUserObject);
		}
	};
	
	private IRule<Tweets> contentType = new DefaultRule<Tweets>(Type.ATTRIBUTE, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]content", "type") {
		public void handleParsedAttribute(XMLParser<Tweets> aParser, int aIndex, String aValue, Tweets aUserObject) {					
			currentTweet.getContent().setType(aValue);
		}
	};
	
	private IRule<Tweets> contentText = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]content") {
		public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {					
			currentTweet.getContent().setValue(aText);	
		}
	};
	
	private IRule<Tweets> lang = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://api.twitter.com/]lang") {
		public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {
			currentTweet.setLanguage(aText);
		}
	};
	
	private IRule<Tweets> author = new DefaultRule<Tweets>(Type.TAG, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]author") {
		public void handleTag(XMLParser<Tweets> aParser, boolean aIsStartTag, Tweets aUserObject) {
			if (aIsStartTag)
				currentTweet.setAuthor(new Author());
			super.handleTag(aParser, aIsStartTag, aUserObject);
		}
	};
	
	private IRule<Tweets> authorName = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]author/[http://www.w3.org/2005/Atom]name") {
		public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {
			currentTweet.getAuthor().setName(aText);
		}
	};
	
	private IRule<Tweets> authorUri = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]author/[http://www.w3.org/2005/Atom]uri") {
		public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {
			currentTweet.getAuthor().setUri(aText);				
		}
	};
	
	@SuppressWarnings("all")
	public SJXPTweetsReader() {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		parser = parser = new XMLParser<Tweets>(
			tweet, published, title, content, contentType, 
			contentText, lang, author, authorName, authorUri
		);
	}
	
	@Override
	public String getParserName() {
		return "SJXP (pull)";
	}

	@Override
	public Tweets read(InputStream anInputStream) 
	throws Exception {
		Tweets _result = new Tweets();	
		parser.parse(anInputStream, "utf-8", _result);
		return _result;
	}	
}
