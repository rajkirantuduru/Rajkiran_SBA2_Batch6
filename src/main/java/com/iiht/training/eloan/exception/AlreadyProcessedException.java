package com.iiht.training.eloan.exception;

public class AlreadyProcessedException extends RuntimeException{

	public AlreadyProcessedException(String message) {
		super(message);
	}
}
