package com.http.exception.runtime;

//调用forward方法后如果调用outputstream写数据，抛出此异常
public class DispatcherRuntimeException extends RuntimeException {

	public DispatcherRuntimeException(String message) {
		super(message);
	}

	
}
