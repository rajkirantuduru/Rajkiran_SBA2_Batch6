package com.iiht.training.eloan.service;

import java.util.List;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.UserDto;

public interface CustomerService {
	
	public UserDto register(UserDto userDto);
	
	public LoanOutputDto applyLoan(Long customerId,
								   LoanDto loanDto);
	
	public LoanOutputDto getStatus(Long loanAppId);
		
	public List<LoanOutputDto> getStatusAll(Long customerId);
}
