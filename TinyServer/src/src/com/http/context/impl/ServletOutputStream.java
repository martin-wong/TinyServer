package com.http.context.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.http.context.Response;
import com.http.exception.runtime.DispatcherRuntimeException;

public class ServletOutputStream extends OutputStream{

	private List bufferList = new ArrayList<ByteBuffer>();
	private Response res;
	 
	protected ServletOutputStream(Response res){
		this.res = res ;
	}

	public List getBufferList() {
		return bufferList;
	}

	@Override
	public void write(int b) throws IOException {
		byte[] buffer = new byte[1];
		buffer[0] = (byte)b ;
		write(buffer,0,1);
	}
	
	public void write(byte[] b) throws IOException {
		 
		int len = b.length;
		write(b, 0, len);
		
	}
	
	public void write(byte[] b ,int off , int len ) throws IOException {
		
		boolean flag = res.getFlag(); 
		if(!flag){
			ByteBuffer buffer = ByteBuffer.allocate(len);
			buffer.put(b, off, len);
			bufferList.add(buffer);
		}else{
			throw new DispatcherRuntimeException("can not use OutputStream.write(..) after forward()");
		}
	}



}
