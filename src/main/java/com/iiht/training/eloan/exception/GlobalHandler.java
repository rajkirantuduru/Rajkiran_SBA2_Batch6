package com.iiht.training.eloan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.iiht.training.eloan.dto.exception.ExceptionResponse;

public class GlobalHandler {
	
	@ExceptionHandler(LoanNotFoundException.class)
	public ResponseEntity<ExceptionResponse> handler(LoanNotFoundException ex){
		ExceptionResponse exception = 
				new ExceptionResponse(ex.getMessage(),
									  System.currentTimeMillis(),
									  HttpStatus.NOT_FOUND.value());
		ResponseEntity<ExceptionResponse> response =
				new ResponseEntity<ExceptionResponse>(exception, HttpStatus.NOT_FOUND);
		return response;
	}
	
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ExceptionResponse> handler(InvalidDataException ex){
		ExceptionResponse exception = 
				new ExceptionResponse(ex.getMessage(),
									  System.currentTimeMillis(),
									  HttpStatus.BAD_REQUEST.value());
		ResponseEntity<ExceptionResponse> response =
				new ResponseEntity<ExceptionResponse>(exception, HttpStatus.BAD_REQUEST);
		return response;
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handler(Exception ex){
		ExceptionResponse exception = 
				new ExceptionResponse(ex.getMessage(),
									  System.currentTimeMillis(),
									  HttpStatus.INTERNAL_SERVER_ERROR.value());
		ResponseEntity<ExceptionResponse> response =
				new ResponseEntity<ExceptionResponse>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
		return response;
	}
}
