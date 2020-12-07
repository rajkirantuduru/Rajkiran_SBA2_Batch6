package com.iiht.training.eloan.service;

import java.util.List;

import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.RejectDto;
import com.iiht.training.eloan.dto.SanctionDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;

public interface ManagerService {
	
	public List<LoanOutputDto> allProcessedLoans();
	
	public RejectDto rejectLoan(Long managerId,
								Long loanAppId,
								RejectDto rejectDto);
	
	
	public SanctionOutputDto sanctionLoan(Long managerId,
										  Long loanAppId,
										  SanctionDto sanctionDto);
}
