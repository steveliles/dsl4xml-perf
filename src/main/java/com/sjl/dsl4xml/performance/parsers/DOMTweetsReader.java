package com.sjl.dsl4xml.performance.parsers;

import java.io.*;
import java.text.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.sjl.dsl4xml.performance.*;
import com.sjl.dsl4xml.performance.model.*;

public class DOMTweetsReader implements TweetsReader {
	
	private DocumentBuilder builder;
	private DateFormat dateFormat;
	
	public DOMTweetsReader() 
	throws Exception {
		DocumentBuilderFactory factory = 
			DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	}
	
	@Override
	public String getParserName() {
		return "W3C DOM";
	}

	public Tweets read(InputStream anInputStream) 
	throws Exception {
		Document _d = builder.parse(anInputStream, "utf-8");
		Tweets _result = new Tweets();
		unmarshall(_d, _result);
		return _result;
	}
	
	public void unmarshall(Document aDoc, Tweets aTo) 
	throws Exception {
		NodeList _nodes = aDoc.getChildNodes().item(0).getChildNodes();
		for (int i=0; i<_nodes.getLength(); i++) {
		    Node _n = _nodes.item(i);
		    if ((_n.getNodeType() == Node.ELEMENT_NODE) && ("entry".equals(_n.getNodeName())))
		    {
				Tweet _tweet = new Tweet();
				aTo.addTweet(_tweet);
				unmarshallEntry((Element)_n, _tweet);
		    }
		}
	}
	
	private void unmarshallEntry(Element aTweetEl, Tweet aTo)
	throws Exception {
		NodeList _nodes = aTweetEl.getChildNodes();
		for (int i=0; i<_nodes.getLength(); i++) {
			Node _n = _nodes.item(i);
			if (_n.getNodeType() == Node.ELEMENT_NODE) {					
				if ("published".equals(_n.getNodeName())) {							
					aTo.setPublished(dateFormat.parse(getPCData(_n)));
				} else if ("title".equals(_n.getNodeName())) {
					aTo.setTitle(getPCData(_n));
				} else if ("content".equals(_n.getNodeName())) {
					Content _content = new Content();
					aTo.setContent(_content);
					unmarshallContent((Element)_n, _content);
				} else if ("twitter:lang".equals(_n.getNodeName())) {
					aTo.setLanguage(getPCData(_n));
				} else if ("author".equals(_n.getNodeName())) {
					Author _author = new Author();
					aTo.setAuthor(_author);
					unmarshallAuthor((Element)_n, _author);
				}
			}
		}
	}
	
	private void unmarshallContent(Element aContentEl, Content aTo) {
		aTo.setType(aContentEl.getAttribute("type"));
		aTo.setValue(getPCData(aContentEl));
	}
	
	private void unmarshallAuthor(Element anAuthorEl, Author aTo) {
		NodeList _nodes = anAuthorEl.getChildNodes();
		for (int i=0; i<_nodes.getLength(); i++) {
			Node _n = _nodes.item(i);
			if ("name".equals(_n.getNodeName())) {
				aTo.setName(getPCData(_n));
			} else if ("uri".equals(_n.getNodeName())) {
				aTo.setUri(getPCData(_n));
			}
		}
	}
	
	private String getPCData(Node aNode) {
		StringBuilder _sb = new StringBuilder();
		if (Node.ELEMENT_NODE == aNode.getNodeType()) {
			NodeList _nodes = aNode.getChildNodes();
			for (int i=0; i<_nodes.getLength(); i++) {
				Node _n = _nodes.item(i);
				if (Node.ELEMENT_NODE == _n.getNodeType()) {
					_sb.append(getPCData(_n));
				} else if (Node.TEXT_NODE == _n.getNodeType()) {
					_sb.append(_n.getNodeValue());
				}
			}
		}
		return _sb.toString();
	}
}
