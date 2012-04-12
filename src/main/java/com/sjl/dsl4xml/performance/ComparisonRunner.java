package com.sjl.dsl4xml.performance;

import java.io.*;

import com.sjl.dsl4xml.performance.Statistics.Entry;
import com.sjl.dsl4xml.performance.impl.*;

public class ComparisonRunner {

	public static void main(String... anArgs) throws Exception {
		ComparisonRunner _cp = new ComparisonRunner();
		_cp.comparePerformance(8, 1000, new File(anArgs[0]));
	}
	
	public void comparePerformance(int aConcurrency, int anIterations, File aResultsFile) 
	throws Exception {
		ComparativeStatistics _stats = new ComparativeStatistics(
			collectStats(new DOMPerformanceTest(aConcurrency, anIterations)),
			//collectStats(new DOMXPathPerformanceTest(aConcurrency, anIterations)),
			collectStats(new PullParserPerformanceTest(aConcurrency, anIterations)),
			collectStats(new Dsl4XmlPerformanceTest(aConcurrency, anIterations)),
			collectStats(new SJXPPerformanceTest(aConcurrency, anIterations)),
			collectStats(new SAXParserPerformanceTest(aConcurrency, anIterations))
		);

		String _chartHtml = _stats.createChartHtml();
		
		Streams.copy(
			new ByteArrayInputStream(_chartHtml.getBytes()), 
			new FileOutputStream(aResultsFile)
		);
	}
	
	private Statistics collectStats(PerformanceTest aTest) throws Exception {
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
