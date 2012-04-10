package com.sjl.dsl4xml.performance.dsl4xml;

import java.io.*;

import com.sjl.dsl4xml.performance.dsl4xml.Statistics.Entry;

public class ComparativePerformanceMain {

	public static void main(String... anArgs) throws Exception {
		int _concurrency = 4;
		int _iterations = 1000;
		
		ComparativeStatistics _stats = new ComparativeStatistics(
			collectStats(new DOMPerformanceTest(_concurrency, _iterations)),
			collectStats(new Dsl4XmlPerformanceTest(_concurrency, _iterations)),
			collectStats(new SJXPPerformanceTest(_concurrency, _iterations)),
			collectStats(new SAXParserPerformanceTest(_concurrency, _iterations))
		);

		String _chartHtml = _stats.createChartHtml();
		
		Streams.copy(
			new ByteArrayInputStream(_chartHtml.getBytes()), 
			new FileOutputStream(new File(anArgs[0]))
		);
	}
	
	private static Statistics collectStats(PerformanceTest aTest) throws Exception {
		System.out.println(aTest.getParserName());
		
		aTest.prepareXml();
		aTest.oneTimeParserSetup();
		
		int _runs = 3;
		Statistics[] _stats = new Statistics[_runs];
		for (int i=0; i<_runs; i++) {
			_stats[i] = aTest.collectStatisticsForMultithreadedParsing();
		}
		
		Statistics _result = new Statistics(_stats[0].getName());
		
		for (int i=0; i<_stats[0].size(); i++) {
			int _threads = 0;
			double _docs = 0;
			double _time = 0;
			
			for (int j=0; j<_runs; j++) {
				Entry _e = _stats[j].getEntry(i);
				_threads = _e.getThreads();
				_docs += _e.getDocs();
				_time += _e.getElapsedNanos();
			}
			
			_result.add(_threads, _time/_runs, _docs/_runs);
		}
		
		return _result;
	}
}
