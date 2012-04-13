package com.sjl.dsl4xml.performance;

import java.util.*;

public class ComparativeStatistics implements Iterable<Statistics> {

	private List<Statistics> stats;
	
	public ComparativeStatistics(List<Statistics> aStats) {
		Map<String, Statistics> _statsByParser = new LinkedHashMap<String, Statistics>();
		for (Statistics _s : aStats) {		
			Statistics _statistics = _statsByParser.get(_s.getName());
			if (_statistics == null) {
				_statsByParser.put(_s.getName(), _s);
			} else {
				_statsByParser.put(_s.getName(), _statistics.combine(_s));
			}
		}
		
		stats = new ArrayList<Statistics>();
		for (Statistics _s : _statsByParser.values()) {
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
		
		boolean _first = true;
		for (Statistics _s : stats) {
			if (_first) {
				_result.add(_s.getEntry(aRowIndex).getThreads());
				_first = false;
			}
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
