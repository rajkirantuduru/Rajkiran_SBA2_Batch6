package com.iiht.training.eloan.dto.exception;

public class ExceptionResponse {
	private String message;
	private Long timeStamp;
	private Integer status;
	public ExceptionResponse() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public ExceptionResponse(String message, Long timeStamp, Integer status) {
		super();
		this.message = message;
		this.timeStamp = timeStamp;
		this.status = status;
	}



	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}

