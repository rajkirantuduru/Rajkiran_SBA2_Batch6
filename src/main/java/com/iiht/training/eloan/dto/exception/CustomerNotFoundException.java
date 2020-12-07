package com.iiht.training.eloan.dto.exception;

public class CustomerNotFoundException extends RuntimeException{

	public CustomerNotFoundException(String message) {
		super(message);
	}
}
