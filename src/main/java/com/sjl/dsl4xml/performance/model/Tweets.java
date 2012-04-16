package com.sjl.dsl4xml.performance.model;

import java.util.*;

import org.simpleframework.xml.*;

@Root(name="feed", strict=false)
public class Tweets implements Iterable<Tweet> {
	
	@ElementList(type=Tweet.class, name="entry", inline=true)
	private List<Tweet> tweets;
	
	public Tweets() {
		tweets = new ArrayList<Tweet>();
	}
	
	public void addTweet(Tweet aTweet) {			
		tweets.add(aTweet);
	}
	
	public Iterator<Tweet> iterator() {
		return tweets.iterator();
	}
	
	public int size() {
		return tweets.size();
	}
	
	public Tweet get(int anIndex) {
		return tweets.get(anIndex);
	}
}