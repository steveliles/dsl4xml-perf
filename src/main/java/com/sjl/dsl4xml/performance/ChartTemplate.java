package com.sjl.dsl4xml.performance;

import java.util.*;

public class ChartTemplate {
	
	private StringBuilder head;
	private StringBuilder data;
	private String foot;
	
	public ChartTemplate() throws Exception {
		String _tpl = Streams.readInputAsUtf8String(getClass().getResourceAsStream("chart-template.html"));
		String[] _parts = _tpl.split("::column-defs::");
		
		head = new StringBuilder(_parts[0]);
		
		_parts = _parts[1].split("::data::");
		
		data = new StringBuilder(_parts[0]);
		foot = _parts[1];
	}

	public void addNumberColumn(String aName) {
		head.append("\r\ndata.addColumn('number', '").append(aName).append("');");
	}

	public void addRow(List<Object> aRowValues) {
		data.append("\r\n");
		data.append("['").append(aRowValues.get(0)).append("',");
		for (int i=1; i<aRowValues.size(); i++) {
			data.append(aRowValues.get(i)).append(",");
		}
		data.setLength(data.length()-1);
		data.append("],");
	}

	public String toHtmlString() {
		data.setLength(data.length()-1); // remove trailing comma
		return head.append(data.toString()).append(foot).toString();
	}
	
	
}
