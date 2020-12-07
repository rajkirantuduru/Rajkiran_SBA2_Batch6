package com.iiht.training.eloan.dto.exception;

public class AlreadyFinalizedException extends RuntimeException{
	
	public AlreadyFinalizedException(String message) {
		super(message);
	}

}
