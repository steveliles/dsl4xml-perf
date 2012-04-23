package com.sjl.dsl4xml.performance;

import java.io.*;
import java.util.concurrent.*;

import com.sjl.dsl4xml.performance.model.*;

public class ReadingThread implements Runnable { 
	
	private byte[] xml;
	private CyclicBarrier gate;
	private TweetsReader reader;
	private int iterations;
		
	public ReadingThread(
		TweetsReader aReader, byte[] anXML,
		int anIterations, CyclicBarrier aGate
	) {
		xml = anXML;
		reader = aReader;
		iterations = anIterations;
		gate = aGate;
	}
	
	@Override
	public void run() {
		try {
			gate.await();
			
			for (int i=0; i<iterations; i++) {
				InputStream _in = new ByteArrayInputStream(xml);
				Tweets _tw = reader.read(_in);
				if (_tw.size() != 15)
					throw new RuntimeException("Expected 15 tweets but read " + _tw.size());
				
			}
		} catch (Exception anExc) {
			anExc.printStackTrace();
			System.err.println("Thread did not complete its batch");
		}
	}
}
