package com.monk.apis.exception;

public class CustomException extends RuntimeException {
	public CustomException(String message) {
		super(message);
	}

	public CustomException(String message, Exception e) {
		super(message, e);
	}
}