package com.sjl.dsl4xml.performance;

import java.util.*;
import java.util.concurrent.*;

public class PerformanceTestRunner {

	interface TweetsReaderFactory {
		public String getParserType();
		public TweetsReader newReader() throws Exception;
	}
	
	private TweetsReaderFactory factory;
	
	public PerformanceTestRunner(TweetsReaderFactory aFactory) {
		factory = aFactory;
	}
	
	public Statistics collectStatistics(byte[] anXml, int aMaxConcurrency, int anIterations) 
	throws Exception {
		System.out.println(factory.getParserType());
		Statistics _stats = new Statistics(factory.getParserType());
		for (int i=1; i<=aMaxConcurrency; i++) {
			long _time = testPerformanceWithConcurrency(anXml, i, anIterations);
			_stats.add(i, _time, i * anIterations);
		}
		return _stats;
	}
	
	private long testPerformanceWithConcurrency(byte[] anXml, int aConcurrency, int anIterations) 
	throws Exception {
		CyclicBarrier _gate = new CyclicBarrier(aConcurrency + 1);
		List<Thread> _threads = new ArrayList<Thread>();
		
		// prepare everything in advance, we only want to time
		// the actual parsing
		for (int i=0; i<aConcurrency; i++) {
			_threads.add(new Thread(new ReadingThread(
				factory.newReader(), anXml, anIterations, _gate 
			)));
		}
		
		// get the threads lined up and waiting to go
		for (Thread _t : _threads) {
			_t.start();
		}
		
		// start timing _just_ before we allow the threads to go at it
		long _start = System.nanoTime();
		_gate.await();
		
		// wait for all parsing threads to finish
		for (Thread _t : _threads) {
			_t.join();
		}
		long _stop = System.nanoTime();
		
		return _stop-_start;
	}
}
