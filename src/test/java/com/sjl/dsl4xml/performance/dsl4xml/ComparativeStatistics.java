package com.sjl.dsl4xml.performance.dsl4xml;

import java.util.*;

public class ComparativeStatistics implements Iterable<Statistics> {

	private List<Statistics> stats;
	
	public ComparativeStatistics(Statistics... aStats) {
		stats = new ArrayList<Statistics>();
		for (Statistics _s : aStats) {
			stats.add(_s);
		}
	}
	
	public Iterator<Statistics> iterator() {
		return stats.iterator();
	}
	
	public String createChartHtml() throws Exception {
		ChartTemplate _t = new ChartTemplate();
		
		for (Statistics _s : stats) {
			_t.addNumberColumn(_s.getName());
		}
		
		for (int i=0; i<getRowCount(); i++) {
			_t.addRow(getRowValues(i));
		}
		
		return _t.toHtmlString();
	}
	
	private List<Object> getRowValues(int aRowIndex) {
		List<Object> _result = new ArrayList<Object>();
		
		_result.add(stats.get(0).getEntry(aRowIndex).getThreads()); // TODO: sorry demeter
		
		for (Statistics _s : stats) {
			Statistics.Entry _e = _s.getEntry(aRowIndex);
			_result.add(_e.getThroughputPerSecond());
		}
		return _result;
	}
	
	private int getRowCount() {
		if (stats.isEmpty())
			return 0;
		
		return stats.get(0).size();
	}
}
