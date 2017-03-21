package com.http.context.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ServletOutputStream extends OutputStream{

	private List buffer = new ArrayList<Byte>();
	
	public List getBuffer() {
		return buffer;
	}

	@Override
	@Deprecated
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	public void write(byte[] b ,int off , int len ) throws IOException {
		int count = len - off ;
		for (int i = 0 ; i < count; i++) {
			buffer.add(b[i+off]);
		}
		
	}



}
