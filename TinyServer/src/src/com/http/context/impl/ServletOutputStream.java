package com.http.context.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ServletOutputStream extends OutputStream{

	private List bufferList = new ArrayList<ByteBuffer>();
	
	public List getBufferList() {
		return bufferList;
	}

	@Override
	public void write(int b) throws IOException {
		byte[] buffer = new byte[1];
		buffer[0] = (byte)b ;
		write(buffer,0,1);
	}
	
	public void write(byte[] b ,int off , int len ) throws IOException {
		 
		ByteBuffer buffer = ByteBuffer.allocate(len);
		buffer.put(b, off, len);
		bufferList.add(buffer);
		
	}



}
