package com.sjl.dsl4xml.performance;

import java.io.*;
import java.util.*;

import com.sjl.dsl4xml.performance.PerformanceTestRunner.TweetsReaderFactory;
import com.sjl.dsl4xml.performance.model.*;
import com.sjl.dsl4xml.performance.parsers.*;

public class PerformanceComparisonMain {

	public static void main(String... anArgs) throws Exception {
		PerformanceComparisonMain _cp = new PerformanceComparisonMain();
		_cp.comparePerformance(4, 50, new File(anArgs[0]));
	}

	public void comparePerformance(int aConcurrency, int anIterations, File aResultsFile) 
	throws Exception {
		byte[] _xml = Streams.readInputIntoByteArray(Tweets.class.getResourceAsStream("twitter-atom.xml"));
		
		List<PerformanceTestRunner> _runners = Arrays.asList(
			newDOMRunner(),	
		    newSAXRunner(),
		    newPullParserRunner(),
			newDsl4XmlPullRunner(),
			newDsl4XmlSAXRunner(),
			newSJXPRunner(),
			newSimpleXMLRunner()
		);
		
		// warm up and discard the first results
		System.out.println("Warming up...");
		List<Statistics> _stats = performNRunsWithEachTestRunner(
			aConcurrency, anIterations, _xml, _runners, 3
		);
		
		// now lets collect the real results
		System.out.println("Testing performance...");
		_stats = performNRunsWithEachTestRunner(
			aConcurrency, anIterations, _xml, _runners, 5
		);
		
		ComparativeStatistics _cs = new ComparativeStatistics(_stats);
		String _chartHtml = _cs.createChartHtml();

		Streams.copy(
			new ByteArrayInputStream(_chartHtml.getBytes()),
			new FileOutputStream(aResultsFile)
		);
	}

	private List<Statistics> performNRunsWithEachTestRunner(
		int aConcurrency, int anIterations, byte[] anXML,
		List<PerformanceTestRunner> aTestRunners, int aNumberOfRuns
	) throws Exception {
		List<Statistics> _stats = new ArrayList<Statistics>();
		for (int i=0; i<aNumberOfRuns; i++) {
			System.out.println("Beginning test-run " + (i+1));
			for (PerformanceTestRunner _r : aTestRunners) {
				_stats.add(_r.collectStatistics(anXML, aConcurrency, anIterations));
			}
		}
		return _stats;
	}
		
	private PerformanceTestRunner newDOMRunner() {
		return new PerformanceTestRunner(
			new TweetsReaderFactory() {
				@Override
				public String getParserType() {
					return "W3C DOM";
				}

				@Override
				public TweetsReader newReader() throws Exception {
					return new DOMTweetsReader();
				}
			}
		);
	}
		
	private PerformanceTestRunner newSAXRunner() {
		return new PerformanceTestRunner(
			new TweetsReaderFactory() {
				@Override
				public String getParserType() {
					return "SAX";
				}

				@Override
				public TweetsReader newReader() throws Exception {
					return new SAXTweetsReader();
				}
			}
		);
	}
	
	private PerformanceTestRunner newPullParserRunner() {
		return new PerformanceTestRunner(
			new TweetsReaderFactory() {
				@Override
				public String getParserType() {
					return "Pull";
				}

				@Override
				public TweetsReader newReader() throws Exception {
					return new PullParserTweetsReader();
				}
			}
		);
	}
	
	private PerformanceTestRunner newDsl4XmlPullRunner() {
		return new PerformanceTestRunner(
			new TweetsReaderFactory() {
				@Override
				public String getParserType() {
					return "dsl4xml (pull)";
				}

				@Override
				public TweetsReader newReader() throws Exception {
					return new Dsl4XmlPullTweetsReader();
				}
			}
		);
	}
	
	private PerformanceTestRunner newDsl4XmlSAXRunner() {
		return new PerformanceTestRunner(
			new TweetsReaderFactory() {
				@Override
				public String getParserType() {
					return "dsl4xml (SAX)";
				}

				@Override
				public TweetsReader newReader() throws Exception {
					return new Dsl4XmlSAXTweetsReader();
				}
			}
		);
	}
	
	private PerformanceTestRunner newSJXPRunner() {
		return new PerformanceTestRunner(
			new TweetsReaderFactory() {
				@Override
				public String getParserType() {
					return "SJXP";
				}

				@Override
				public TweetsReader newReader() throws Exception {
					return new SJXPTweetsReader();
				}
			}
		);
	}
	
	private PerformanceTestRunner newSimpleXMLRunner() {
		return new PerformanceTestRunner(
			new TweetsReaderFactory() {
				@Override
				public String getParserType() {
					return "SimpleXML";
				}

				@Override
				public TweetsReader newReader() throws Exception {
					return new SimpleXMLTweetsReader();
				}
			}
		);
	}
}
