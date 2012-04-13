package com.sjl.dsl4xml.performance.parsers;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;

import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.performance.model.*;

public class DOMXPathTweetsReader implements TweetsReader {
	
	private DocumentBuilder builder;
	private XPathFactory factory;
	
	private XPathExpression entry;
	private XPathExpression published;
	private XPathExpression title;
	private XPathExpression contentType;
	private XPathExpression content;
	private XPathExpression lang;
	private XPathExpression authorName;
	private XPathExpression authorUri;
	
	private DateFormat dateFormat;
	
	public DOMXPathTweetsReader() 
	throws Exception {
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		factory = XPathFactory.newInstance();
		
		entry = factory.newXPath().compile("/feed/entry");
		published = factory.newXPath().compile(".//published");
		title = factory.newXPath().compile(".//title");
		contentType = factory.newXPath().compile(".//content/@type");
		content = factory.newXPath().compile(".//content");
		lang = factory.newXPath().compile(".//twitter:lang");
		authorName = factory.newXPath().compile(".//author/name");
		authorUri = factory.newXPath().compile(".//author/uri");
		
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	}
	
	@Override
	public String getParserName() {
		return "W3C DOM/XPath";
	}



	@Override
	public Tweets read(InputStream anInputStream)
	throws Exception {
		Tweets _result = new Tweets();
		Document _document = builder.parse(anInputStream);

		NodeList _entries = (NodeList) entry.evaluate(_document, XPathConstants.NODESET);					
		for (int i=0; i<_entries.getLength(); i++) {
			Tweet _tweet = new Tweet();
			_result.addTweet(_tweet);
			
			Node _entryNode = _entries.item(i);
			
			_tweet.setPublished(getPublishedDate(_entryNode));
			_tweet.setTitle(title.evaluate(_entryNode));
			_tweet.setLanguage(lang.evaluate(_entryNode));
			
			Content _c = new Content();
			_tweet.setContent(_c);
			
			_c.setType(contentType.evaluate(_entryNode));
			_c.setValue(content.evaluate(_entryNode));
			
			Author _a = new Author();
			_tweet.setAuthor(_a);
			
			_a.setName(authorName.evaluate(_entryNode));
			_a.setUri(authorUri.evaluate(_entryNode));
		}
		
		return _result;
	}
	
	private Date getPublishedDate(Node aNode) 
	throws Exception {
		return dateFormat.parse(published.evaluate(aNode));
	}
}
