package com.sjl.dsl4xml.performance.impl;

import java.io.*;
import java.text.*;
import java.util.concurrent.*;

import org.xmlpull.v1.*;

import com.sjl.dsl4xml.performance.*;

public class PullParserPerformanceTest extends PerformanceTest {
	
	private static final Tag ENTRY_TAG = new Tag(null, "entry");
	private static final Tag PUBLISHED_TAG = new Tag(null, "published");
	private static final Tag TITLE_TAG = new Tag(null, "title");
	private static final Tag CONTENT_TAG = new Tag(null, "content");
	private static final Tag LANG_TAG = new Tag("twitter", "lang");
	private static final Tag AUTHOR_TAG = new Tag(null, "author");
	private static final Tag NAME_TAG = new Tag(null, "name");
	private static final Tag URI_TAG = new Tag(null, "uri");
	
	public PullParserPerformanceTest(int aMaxConcurrency, int anIterationsPerThread) {
		super(aMaxConcurrency, anIterationsPerThread);
	}
	
	public PullParserPerformanceTest() {
		super();
	}
	
	@Override
	protected String getParserName() {
		return "Pull-Parser";
	}

	@Override
	protected ReadingThread newReadingThread(CyclicBarrier aGate)
	throws Exception {
		final DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		final XmlPullParserFactory _f = XmlPullParserFactory.newInstance();
		_f.setNamespaceAware(true);
		
		return new ReadingThread(aGate) {
			private Tweets tweets;
			private Tweet currentTweet;
			private Author currentAuthor;
			
			public Tweets read(InputStream anInputStream) {
				try {
					XmlPullParser _p = _f.newPullParser();
					_p.setInput(anInputStream, "utf-8");
					return parse(_p);
				} catch (Exception anExc) {
					throw new RuntimeException(anExc);
				}
			}
			
			private Tweets parse(XmlPullParser aParser) 
			throws Exception {
				tweets = new Tweets();
				currentTweet = null;
				 
				int _e = aParser.next();
				while (_e != XmlPullParser.END_DOCUMENT) {
					if (_e == XmlPullParser.START_TAG) {
						startTag(new Tag(aParser.getPrefix(), aParser.getName()), aParser);
					}
					_e = aParser.next();
				}
				
				return tweets;
			}
			
			private void startTag(Tag aTag, XmlPullParser aParser)
			throws Exception {
				if (ENTRY_TAG.equals(aTag)) {
					tweets.addTweet(currentTweet = new Tweet());
				} else if (PUBLISHED_TAG.equals(aTag)) {
					aParser.next();
					currentTweet.setPublished(_dateFormat.parse(aParser.getText()));
				} else if ((TITLE_TAG.equals(aTag)) && (currentTweet != null)) {
					aParser.next();
					currentTweet.setTitle(aParser.getText());
				} else if (CONTENT_TAG.equals(aTag)) {
					Content _c = new Content();
					_c.setType(aParser.getAttributeValue(null, "type"));
					aParser.next();
					_c.setValue(aParser.getText());
					currentTweet.setContent(_c);
				} else if (LANG_TAG.equals(aTag)) {
					aParser.next();
					currentTweet.setLanguage(aParser.getText());
				} else if (AUTHOR_TAG.equals(aTag)) {
					currentTweet.setAuthor(currentAuthor = new Author());
				} else if (NAME_TAG.equals(aTag)) {
					aParser.next();
					currentAuthor.setName(aParser.getText());
				} else if (URI_TAG.equals(aTag)) {
					aParser.next();
					currentAuthor.setUri(aParser.getText());
				}
			}
		};
	}
	
	private static class Tag {
		private String ns;
		private String name;
		
		public Tag(String aNamespace, String aName) {
			ns = aNamespace;
			name = aName;
		}
		
		public boolean equals(Object anOther) {
			if (anOther instanceof Tag) {
				Tag _other = (Tag) anOther;
				return (
					((ns != null) ? ns.equals(_other.ns) : _other.ns == null) &&
					((name != null) ? name.equals(_other.name) : _other.name == null)
				);
			}
			return false;
		}
		
		public int hashCode() {
			return ns.hashCode() ^ name.hashCode();
		}
	}
}
