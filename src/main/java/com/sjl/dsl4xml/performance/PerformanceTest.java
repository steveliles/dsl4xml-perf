package com.sjl.dsl4xml.performance;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.junit.*;

public abstract class PerformanceTest {

	private int maxConcurrency;
	private int iterationsPerThread;
	private byte[] xml;

	public PerformanceTest(int aMaxConcurrency, int anIterationsPerThread) {
		maxConcurrency = aMaxConcurrency;
		iterationsPerThread = anIterationsPerThread;
	}
	
	public PerformanceTest() {
		this(8, 50);
	}
	
	protected abstract ReadingThread newReadingThread(CyclicBarrier aGate)
	throws Exception;
	
	protected abstract String getParserName();
	
	@Before
	public void oneTimeParserSetup() throws Exception {
		// may be overridden to perform one time setup of parser
	}
	
	@Before
	public void prepareXml() throws Exception {
		xml = Streams.readInputIntoByteArray(Tweets.class.getResourceAsStream("twitter-atom.xml"));
	}
	
	@Test
	public void testMultithreadedParsing() throws Exception {
		Statistics _stats = collectStatisticsForMultithreadedParsing();
		System.out.println(_stats);
	}
	
	public Statistics collectStatisticsForMultithreadedParsing() throws Exception {		
		Statistics _stats = new Statistics(getParserName());
		waitForGC();
		for (int i=1; i<=maxConcurrency; i++) {
			long _start = System.nanoTime();
			testWithNThreads(i);
			long _stop = System.nanoTime();
			
			_stats.add(i, _stop-_start, i * iterationsPerThread);
		}
		return _stats;
	}
	
	private void waitForGC() {
		System.gc();
		try { Thread.sleep(200L); } catch (InterruptedException anExc){}
	}
	
	private void testWithNThreads(int aNumberOfThreads) throws Exception {
		List<Thread> _threads = new ArrayList<Thread>();
		CyclicBarrier _gate = new CyclicBarrier(aNumberOfThreads);
		
		for (int i=0; i<aNumberOfThreads; i++) {
			_threads.add(new Thread(newReadingThread(_gate)));
		}
		
		for (Thread _t : _threads) {
			_t.start();
		}
		
		for (Thread _t : _threads) {
			_t.join();
		}
	}
	
	public abstract class ReadingThread implements Runnable {
		private CyclicBarrier gate;
		
		public ReadingThread(CyclicBarrier aGate) {
			gate = aGate;
		}
		
		@Override
		public void run() {
			try {
				gate.await();
				
				for (int i=0; i<iterationsPerThread; i++) {
					InputStream _in = new ByteArrayInputStream(xml);
					Tweets _tw = read(_in);
					
					// do some simple correctness tests...
					
					if (_tw.size() != 15)
						throw new RuntimeException("Expected 15 tweets but read " + _tw.size());
					
					if (!"LowellSunSports (Lowell Sun Sports)".equals(_tw.get(14).getAuthor().getName()))
						throw new RuntimeException("Expected correct name, but got " + _tw.get(14).getAuthor().getName());
				}
			} catch (Exception anExc) {
				anExc.printStackTrace();
				System.err.println("Thread did not complete its batch");
			}
		}
		
		public abstract Tweets read(InputStream anInputStream);
	}
}
