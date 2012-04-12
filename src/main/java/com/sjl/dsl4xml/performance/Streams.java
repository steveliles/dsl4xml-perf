package com.sjl.dsl4xml.performance;

import java.io.*;
import java.nio.charset.*;

public class Streams {

	public static String readInputAsUtf8String(InputStream anInput) throws Exception {
		return new String(readInputIntoByteArray(anInput), Charset.forName("utf-8"));
	}
	
	public static byte[] readInputIntoByteArray(InputStream anInput) throws IOException {
		ByteArrayOutputStream _out = new ByteArrayOutputStream();
		try {
			copy(anInput, _out);
		} finally {
			if (anInput != null)
				anInput.close();
		}
		
		return _out.toByteArray();
	}
	
	public static void copy(InputStream anInput, OutputStream anOutput) 
	throws IOException {
		byte[] _buffer = new byte[256];
		int _length;

		while ((_length = anInput.read(_buffer)) > -1) {
			anOutput.write(_buffer, 0, _length);
		}
		anOutput.flush();
	}
	
}
