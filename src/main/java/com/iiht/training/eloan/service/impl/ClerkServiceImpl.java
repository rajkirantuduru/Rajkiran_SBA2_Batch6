package com.iiht.training.eloan.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.ClerkNotFoundException;
import com.iiht.training.eloan.dto.exception.CustomerNotFoundException;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.ProcessingInfo;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.AlreadyProcessedException;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.ClerkService;

@Service
public class ClerkServiceImpl implements ClerkService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository pProcessingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	@Override
	public List<LoanOutputDto> allAppliedLoans() {
		List<Loan> Loans = this.loanRepository.findAllByStatus(0)
				.orElseThrow(() -> new LoanNotFoundException("No applied Loans Found"));

		List<LoanOutputDto> loanOutputDto = new ArrayList<LoanOutputDto>();
		for (Loan loan : Loans) {
			Users user = this.usersRepository.findById(loan.getCustomerId())
					.orElseThrow(() -> new CustomerNotFoundException("Customer not Found"));
			UserDto userDto = this.convertUserEntityUserToDto(user);
			LoanDto loanDto = this.convertEntityToLoanDto(loan);
			LoanOutputDto loanOutDto = this.convertLoanEntityToLoanOutputDto(loan, userDto, loanDto);
			loanOutputDto.add(loanOutDto);
		}
		return loanOutputDto;
	}

	@Override
	public ProcessingDto processLoan(Long clerkId, Long loanAppId, ProcessingDto processingDto) {
		this.usersRepository.findByIdAndRole(clerkId,"Clerk").orElseThrow(() -> new ClerkNotFoundException("Clerk Not Found"));
		Loan loan = this.loanRepository.findById(loanAppId).orElseThrow(() -> new LoanNotFoundException("Loan Not Found"));
		if (loan.getStatus() != 0) {
			throw new AlreadyProcessedException("Already Processed Loan");
		}
		ProcessingInfo prinfo = this.covertInputProcessingDtoToEntity(clerkId,loanAppId,processingDto);
		// save entity in DB : returns the copy of newly added record back
		ProcessingInfo newProcesingInfo= this.pProcessingInfoRepository.save(prinfo);
		this.loanRepository.setStatusForLoan(1, loanAppId);
		// convert entity into output dto
		ProcessingDto ProcessingDto = this.convertEntityToProcessingDto(newProcesingInfo);
		return ProcessingDto;
	}
	
//Utility Methods.
private UserDto convertUserEntityUserToDto(Users user) {
	UserDto userDto = new UserDto();
	
	userDto.setFirstName(user.getFirstName());
	userDto.setLastName(user.getLastName());
	userDto.setEmail(user.getEmail());
	userDto.setMobile(user.getMobile());
	userDto.setId(user.getId());
	userDto.setRole(user.getRole());
	
	return userDto;
}

private LoanDto convertEntityToLoanDto(Loan loan) {
	LoanDto loanDto = new LoanDto();
	
	loanDto.setLoanName(loan.getLoanName());
	loanDto.setLoanAmount(loan.getLoanAmount());
	loanDto.setLoanApplicationDate(loan.getLoanApplicationDate());
	loanDto.setBusinessStructure(loan.getBusinessStructure());
	loanDto.setBillingIndicator(loan.getBillingIndicator());
	loanDto.setTaxIndicator(loan.getTaxIndicator());
	
	return loanDto;
}
private LoanOutputDto convertLoanEntityToLoanOutputDto(Loan loan, UserDto userDto, LoanDto loanDto) {
	
	LoanOutputDto loanOutputDto = new LoanOutputDto();
	loanOutputDto.setLoanAppId(loan.getId());
	loanOutputDto.setCustomerId(loan.getCustomerId());
	loanOutputDto.setLoanDto(loanDto);
	Integer status = loan.getStatus();
	String Revstatus = null; 
	
	switch(status) {
	case 0 :Revstatus = "Applied"; break;
	case 1 :Revstatus = "Processed"; break;
	case 2 :Revstatus = "Sanctioned"; break;
	case -1 :Revstatus = "Rejected"; break;
	}
	
	loanOutputDto.setUserDto(userDto);
	loanOutputDto.setStatus(Revstatus);
	loanOutputDto.setRemark(loan.getRemark());
	return loanOutputDto;
}

protected ProcessingInfo covertInputProcessingDtoToEntity(Long clerkId, Long loanAppId, ProcessingDto processingDto) {
	ProcessingInfo info = new ProcessingInfo();
	info.setLoanAppId(loanAppId);
	info.setLoanClerkId(clerkId);
	info.setAcresOfLand(processingDto.getAcresOfLand());
	info.setLandValue(processingDto.getLandValue());
	info.setAppraisedBy(processingDto.getAppraisedBy());
	info.setValuationDate(processingDto.getValuationDate());
	info.setAddressOfProperty(processingDto.getAddressOfProperty());
	info.setSuggestedAmountOfLoan(processingDto.getSuggestedAmountOfLoan());
	return info;
	
}
private ProcessingDto convertEntityToProcessingDto(ProcessingInfo processingInfo) {
	
	ProcessingDto processingDto = new ProcessingDto();
	processingDto.setAcresOfLand(processingInfo.getAcresOfLand());
	processingDto.setLandValue(processingInfo.getLandValue());
	processingDto.setAppraisedBy(processingInfo.getAppraisedBy());
	processingDto.setValuationDate(processingInfo.getValuationDate());
	processingDto.setAddressOfProperty(processingInfo.getAddressOfProperty());
	processingDto.setSuggestedAmountOfLoan(processingInfo.getSuggestedAmountOfLoan());
	
	return processingDto;
}



}
