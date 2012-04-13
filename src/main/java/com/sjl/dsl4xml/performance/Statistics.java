package com.sjl.dsl4xml.performance;

import java.util.*;

public class Statistics implements Iterable<Statistics.Entry> {

	public class Entry {
		private int threads;
		private double time;
		private double docs;
		
		public Entry(int aThreads, double aTime, double aDocs) {
			threads = aThreads;
			time = aTime;
			docs = aDocs;
		}
		
		public int getThreads() {
			return threads;
		}
		
		public double getElapsedNanos() {
			return time;
		}

		public double getDocs() {
			return docs;
		}
		
		public double getThroughputPerSecond() {
			return (1000000000d / time) * docs;
		}
		
		public String toString() {
			return getThroughputPerSecond() + " docs/sec with " + threads + " threads";
		}
	}
	
	private String name;
	private List<Entry> entries;
	
	public Statistics(String aName) {
		name = aName;
		entries = new ArrayList<Entry>();
	}
	
	public String getName() {
		return name;
	}
	
	public void add(int aThreadCount, double aNanos, double aDocs) {
		entries.add(new Entry(aThreadCount, aNanos, aDocs));
	}

	@Override
	public Iterator<Entry> iterator() {
		return entries.iterator();
	}
	
	public Entry getEntry(int anIndex) {
		return entries.get(anIndex);
	}
	
	public int size() {
		return entries.size();
	}
	
	public Statistics combine(Statistics aStatistics) {
		if (!name.equals(aStatistics.name))
			throw new IllegalArgumentException("can't add " + aStatistics.name + " to " + name);
		if (entries.size() != aStatistics.size())
			throw new IllegalArgumentException("mismatched stats - I have " + size() + " but other has " + aStatistics.size());
		
		Statistics _result = new Statistics(name);
		for (int i=0; i<size(); i++) {
			Entry _mine = entries.get(i);
			Entry _other = aStatistics.entries.get(i);
			
			_result.add(_mine.threads, _mine.time + _other.time, _mine.docs + _other.docs);
		}
		return _result;
	}
	
	public String toString() {
		StringBuilder _sb = new StringBuilder();
		_sb.append("statistics for ").append(name).append("\r\n");
		for (Entry _e : entries) {
			_sb.append(_e).append("\r\n");
		}
		return _sb.toString();
	}
}
