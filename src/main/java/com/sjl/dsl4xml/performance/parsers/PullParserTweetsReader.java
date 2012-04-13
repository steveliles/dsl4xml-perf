package com.sjl.dsl4xml.performance.parsers;

import java.io.*;
import java.text.*;

import org.xmlpull.v1.*;

import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.performance.model.*;

public class PullParserTweetsReader implements TweetsReader {

	private DateFormat dateFormat;
	private XmlPullParserFactory f;
	private Tweets tweets;
	private Tweet currentTweet;
	private Author currentAuthor;
	
	public PullParserTweetsReader() 
	throws Exception {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		f = XmlPullParserFactory.newInstance();
		f.setNamespaceAware(true);
	}
	
	@Override
	public String getParserName() {
		return "Pull-Parser";
	}

	@Override
	public Tweets read(InputStream anInputStream) throws Exception {
		XmlPullParser _p = f.newPullParser();
		_p.setInput(anInputStream, "utf-8");
		return parse(_p);
	}
	
	private Tweets parse(XmlPullParser aParser) 
	throws Exception {
		tweets = new Tweets();
		 
		int _e = aParser.next();
		while (_e != XmlPullParser.END_DOCUMENT) {
			if (_e == XmlPullParser.START_TAG) {
				startTag(aParser.getPrefix(), aParser.getName(), aParser);
			}
			_e = aParser.next();
		}
		
		return tweets;
	}
	
	private void startTag(String aPrefix, String aName, XmlPullParser aParser)
	throws Exception {
		if ("entry".equals(aName)) {
			tweets.addTweet(currentTweet = new Tweet());
		} else if ("published".equals(aName)) {
			aParser.next();
			currentTweet.setPublished(dateFormat.parse(aParser.getText()));
		} else if (("title".equals(aName)) && (currentTweet != null)) {
			aParser.next();
			currentTweet.setTitle(aParser.getText());
		} else if ("content".equals(aName)) {
			Content _c = new Content();
			_c.setType(aParser.getAttributeValue(null, "type"));
			aParser.next();
			_c.setValue(aParser.getText());
			currentTweet.setContent(_c);
		} else if ("lang".equals(aName)) {
			aParser.next();
			currentTweet.setLanguage(aParser.getText());
		} else if ("author".equals(aName)) {
			currentTweet.setAuthor(currentAuthor = new Author());
		} else if ("name".equals(aName)) {
			aParser.next();
			currentAuthor.setName(aParser.getText());
		} else if ("uri".equals(aName)) {
			aParser.next();
			currentAuthor.setUri(aParser.getText());
		}
	}
}
