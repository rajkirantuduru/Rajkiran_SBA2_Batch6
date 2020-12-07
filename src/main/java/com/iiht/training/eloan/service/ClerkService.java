package com.iiht.training.eloan.service;

import java.util.List;

import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;

public interface ClerkService {
	
	public List<LoanOutputDto> allAppliedLoans();
		
	public ProcessingDto processLoan(Long clerkId,
									 Long loanAppId,
									 ProcessingDto processingDto);
}
