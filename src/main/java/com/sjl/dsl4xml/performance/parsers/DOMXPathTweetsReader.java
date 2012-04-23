package com.sjl.dsl4xml.performance.parsers;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.namespace.*;
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
		DocumentBuilderFactory _dbf = DocumentBuilderFactory.newInstance();
		_dbf.setNamespaceAware(true);
		builder = _dbf.newDocumentBuilder();
		factory = XPathFactory.newInstance();
		
		NamespaceContext _ctx = new NamespaceContext() {
            public String getNamespaceURI(String aPrefix) {
                String _uri;
                if (aPrefix.equals("atom"))
                    _uri = "http://www.w3.org/2005/Atom";
                else if (aPrefix.equals("twitter"))
                    _uri = "http://api.twitter.com/";
                else
                    _uri = null;
                return _uri;
            }

			@Override
			public String getPrefix(String aArg0) {
				return null;
			}

			@Override
			@SuppressWarnings("rawtypes")
			public Iterator getPrefixes(String aArg0) {
				return null;
			}
        };
		
        entry = newXPath(factory, _ctx, "/atom:feed/atom:entry");
		published = newXPath(factory, _ctx, ".//atom:published");
		title = newXPath(factory, _ctx, ".//atom:title");
		contentType = newXPath(factory, _ctx, ".//atom:content/@type");
		content = newXPath(factory, _ctx, ".//atom:content");
		lang = newXPath(factory, _ctx, ".//twitter:lang");
		authorName = newXPath(factory, _ctx, ".//atom:author/atom:name");
		authorUri = newXPath(factory, _ctx, ".//atom:author/atom:uri");
		
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	}
	
	private XPathExpression newXPath(XPathFactory aFactory, NamespaceContext aCtx, String anXPath) 
	throws Exception {
		XPath _xp = factory.newXPath();
        _xp.setNamespaceContext(aCtx);
        return _xp.compile(anXPath);
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
