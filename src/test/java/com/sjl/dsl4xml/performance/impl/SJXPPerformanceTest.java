package com.sjl.dsl4xml.performance.impl;

import java.io.*;
import java.text.*;
import java.util.concurrent.*;

import com.sjl.dsl4xml.performance.*;
import com.thebuzzmedia.sjxp.*;
import com.thebuzzmedia.sjxp.rule.*;
import com.thebuzzmedia.sjxp.rule.IRule.Type;

public class SJXPPerformanceTest extends PerformanceTest {

	public SJXPPerformanceTest(int aMaxConcurrency, int anIterationsPerThread) {
		super(aMaxConcurrency, anIterationsPerThread);
	}
	
	public SJXPPerformanceTest() {
		super();
	}
	
	@Override
	protected String getParserName() {
		return "SJXP (pull-parsing)";
	}

	@Override
	protected ReadingThread newReadingThread(CyclicBarrier aGate) {
		return new ReadingThread(aGate) {
			Tweet currentTweet;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			
			IRule<Tweets> _tweet = new DefaultRule<Tweets>(Type.TAG, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry") {
				public void handleTag(XMLParser<Tweets> aParser, boolean aIsStartTag, Tweets aUserObject) {
					if (aIsStartTag)
						aUserObject.addTweet(currentTweet = new Tweet());
				}	
			};
			
			IRule<Tweets> _published = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]published") {
				public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {
					try {					
						currentTweet.setPublished(dateFormat.parse(aText));
					} catch (ParseException anExc) {
						throw new XMLParserException("date-parsing problem", anExc);
					}
				}			
			}; 
			
			IRule<Tweets> _title = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]title") {
				public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {
					currentTweet.setTitle(aText);
				}			
			};
			
			IRule<Tweets> _content = new DefaultRule<Tweets>(Type.TAG, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]content") {
				public void handleTag(XMLParser<Tweets> aParser, boolean aIsStartTag, Tweets aUserObject) {
					if (aIsStartTag)
						currentTweet.setContent(new Content());
					super.handleTag(aParser, aIsStartTag, aUserObject);
				}
			};
			
			IRule<Tweets> _contentType = new DefaultRule<Tweets>(Type.ATTRIBUTE, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]content", "type") {
				public void handleParsedAttribute(XMLParser<Tweets> aParser, int aIndex, String aValue, Tweets aUserObject) {					
					currentTweet.getContent().setType(aValue);
				}
			};
			
			IRule<Tweets> _contentText = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]content") {
				public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {					
					currentTweet.getContent().setValue(aText);	
				}
			};
			
			IRule<Tweets> _lang = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://api.twitter.com/]lang") {
				public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {
					currentTweet.setLanguage(aText);
				}
			};
			
			IRule<Tweets> _author = new DefaultRule<Tweets>(Type.TAG, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]author") {
				public void handleTag(XMLParser<Tweets> aParser, boolean aIsStartTag, Tweets aUserObject) {
					if (aIsStartTag)
						currentTweet.setAuthor(new Author());
					super.handleTag(aParser, aIsStartTag, aUserObject);
				}
			};
			
			IRule<Tweets> _authorName = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]author/[http://www.w3.org/2005/Atom]name") {
				public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {
					currentTweet.getAuthor().setName(aText);
				}
			};
			
			IRule<Tweets> _authorUri = new DefaultRule<Tweets>(Type.CHARACTER, "/[http://www.w3.org/2005/Atom]feed/[http://www.w3.org/2005/Atom]entry/[http://www.w3.org/2005/Atom]author/[http://www.w3.org/2005/Atom]uri") {
				public void handleParsedCharacters(XMLParser<Tweets> aParser, String aText, Tweets aUserObject) {
					currentTweet.getAuthor().setUri(aText);				
				}
			}; 
			
			@SuppressWarnings("all")
			XMLParser<Tweets> parser = new XMLParser<Tweets>(_tweet, _published, _title, _content, _contentType, _contentText, _lang, _author, _authorName, _authorUri); 
			
			@Override
			public Tweets read(InputStream anInputStream) {
				try {
					Tweets _result = new Tweets();	
					parser.parse(anInputStream, "utf-8", _result);
					return _result;
				} catch (Exception anExc) {
					throw new RuntimeException(anExc);
				}
			}
		};
	}	
	
}
