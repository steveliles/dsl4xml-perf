package com.sjl.dsl4xml.performance.dsl4xml;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.junit.*;

import com.sjl.dsl4xml.performance.*;

public abstract class PerformanceTest {

	private static final int MAX_CONCURRENCY = 25;
	private static final int ITERATIONS_PER_THREAD = 100;
	
	private byte[] xml;
	
	protected abstract ReadingThread newReadingThread(CyclicBarrier aGate);
	
	@Before
	public void oneTimeSetup() throws Exception {
		xml = readInputIntoByteArray();
	}
	
	@Test
	public void testMultithreadedParsing() throws Exception {
		for (int i=1; i<=MAX_CONCURRENCY; i++) {
			long _start = System.nanoTime();
			testWithNThreads(i);
			long _stop = System.nanoTime();
			
			System.out.println((_stop - _start) + "ns with " + i + " threads.");
		}
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
	
	private byte[] readInputIntoByteArray() throws IOException {
		ByteArrayOutputStream _out = new ByteArrayOutputStream();
		InputStream _in = Tweets.class.getResourceAsStream("twitter-atom.xml");
		try {
			copy(_in, _out);
		} finally {
			if (_in != null)
				_in.close();
		}
		
		return _out.toByteArray();
	}
	
	private void copy(InputStream anInput, OutputStream anOutput) 
	throws IOException {
		byte[] _buffer = new byte[256];
		int _length;

		while ((_length = anInput.read(_buffer)) > -1) {
			anOutput.write(_buffer, 0, _length);
		}
		anOutput.flush();
	}
	
	abstract class ReadingThread implements Runnable {
		private CyclicBarrier gate;
		
		public ReadingThread(CyclicBarrier aGate) {
			gate = aGate;
		}
		
		@Override
		public void run() {
			try {
				gate.await();
				
				for (int i=0; i<ITERATIONS_PER_THREAD; i++) {
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
