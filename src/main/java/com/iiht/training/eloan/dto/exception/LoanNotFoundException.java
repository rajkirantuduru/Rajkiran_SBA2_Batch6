package com.iiht.training.eloan.dto.exception;

public class LoanNotFoundException extends RuntimeException{

	public LoanNotFoundException(String message) {
		super(message);
	}
}