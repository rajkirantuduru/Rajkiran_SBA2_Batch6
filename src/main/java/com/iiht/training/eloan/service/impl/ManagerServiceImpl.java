package com.iiht.training.eloan.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.RejectDto;
import com.iiht.training.eloan.dto.SanctionDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.AlreadyFinalizedException;
import com.iiht.training.eloan.dto.exception.CustomerNotFoundException;
import com.iiht.training.eloan.dto.exception.ManagerNotFoundException;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.ProcessingInfo;
import com.iiht.training.eloan.entity.SanctionInfo;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.ManagerService;

@Service
public class ManagerServiceImpl implements ManagerService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository pProcessingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	@Override
	public List<LoanOutputDto> allProcessedLoans() {
		List<Loan> allProcessedLoans = this.loanRepository.findAllByStatus(1).orElseThrow(() -> new LoanNotFoundException("No processed Loans Found"));
		List<LoanOutputDto> allProcessedLoanOutputDto = new ArrayList<LoanOutputDto>();
		
		for (Loan processedLoan:allProcessedLoans) {
			Users user = this.usersRepository.findById(processedLoan.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException("Customer not Found"));
			ProcessingInfo processingInfo = this.pProcessingInfoRepository.findByLoanAppId(processedLoan.getId()).orElse(null);
			// convert entity into output DTO
			UserDto userDto = this.convertCustomerEntityToOutputDto(user);
			LoanDto loanDto = this.convertEntityToLoanDto(processedLoan);
			ProcessingDto processingDto = this.convertProcessingInfoToDto(processingInfo);
			LoanOutputDto loanOutputDto = this.convertLoanEntityToOutputDto(processedLoan, userDto, loanDto, processingDto, null); 
			allProcessedLoanOutputDto.add(loanOutputDto);
		}
		return allProcessedLoanOutputDto;
	}

	@Override
	public RejectDto rejectLoan(Long managerId, Long loanAppId, RejectDto rejectDto) {
		this.usersRepository.findByIdAndRole(managerId, "Manager").orElseThrow(() -> new ManagerNotFoundException("Manager not Found"));
		Loan loan = this.loanRepository.findById(loanAppId).orElseThrow(() -> new LoanNotFoundException("Loan Not Found"));
		if (loan.getStatus()!=1) {
			throw new AlreadyFinalizedException("Loan not in Processed Status, might have been Rejected/Approved");
		}
		RejectDto rejectOutputDto = new RejectDto();;
		
		this.loanRepository.setStatusRemark("Manager "+managerId+" : "+rejectDto.getRemark(), -1, loanAppId);
		
		rejectOutputDto.setRemark("Manager "+managerId+" : "+rejectDto.getRemark());
		
		return rejectOutputDto;
	}

	@Override
	public SanctionOutputDto sanctionLoan(Long managerId, Long loanAppId, SanctionDto sanctionDto) {
		this.usersRepository.findByIdAndRole(managerId, "Manager").orElseThrow(() -> new ManagerNotFoundException("Manager not Found"));
		Loan loan = this.loanRepository.findById(loanAppId).orElseThrow(() -> new LoanNotFoundException("Loan Not Found"));
		if (loan.getStatus()!=1) {
			throw new AlreadyFinalizedException("Loan not in Processed Status, might have been Rejected/Approved");
		}
		SanctionInfo sanctionInfo = this.convertSanctionDtoToEntity(managerId, loanAppId, sanctionDto);
		
		SanctionInfo newSanctionInfo = this.sanctionInfoRepository.save(sanctionInfo);
		this.loanRepository.setStatusForLoan(2, newSanctionInfo.getLoanAppId());
		
		SanctionOutputDto sanctionOutputDto = this.convertSanctionInfoToOutputDto(newSanctionInfo);
		
		return sanctionOutputDto;
	}
	
	
	
	//Utility Methods
	
	private LoanOutputDto convertLoanEntityToOutputDto(Loan loan, UserDto userDto, LoanDto loanDto, ProcessingDto processingDto,
			SanctionOutputDto sanctionOutputDto) {
		LoanOutputDto loanOutputDto = new LoanOutputDto();
		String status;
		
		loanOutputDto.setLoanAppId(loan.getId());
		loanOutputDto.setCustomerId(loan.getCustomerId());
		loanOutputDto.setUserDto(userDto);
		loanOutputDto.setLoanDto(loanDto);
		loanOutputDto.setProcessingDto(processingDto);
		loanOutputDto.setSanctionOutputDto(sanctionOutputDto);
		if (loan.getStatus()==0) {
			status = "Applied";
		} else if(loan.getStatus()==1) {
			status = "Processed";
		} else if(loan.getStatus()==2) {
			status = "Sanctioned";
		} else if(loan.getStatus()==-1) {
			status = "Rejected";
		} else {
			status = "";
		}
		loanOutputDto.setStatus(status);
		loanOutputDto.setRemark(loan.getRemark());
		
		return loanOutputDto;
	}
	
	private ProcessingDto convertProcessingInfoToDto(ProcessingInfo processingInfo) {
		if (processingInfo!=null) {
			ProcessingDto processingDto = new ProcessingDto();
			
			processingDto.setAcresOfLand(processingInfo.getAcresOfLand());
			processingDto.setLandValue(processingInfo.getLandValue());
			processingDto.setAppraisedBy(processingInfo.getAppraisedBy());
			processingDto.setValuationDate(processingInfo.getValuationDate());
			processingDto.setAddressOfProperty(processingInfo.getAddressOfProperty());
			processingDto.setSuggestedAmountOfLoan(processingInfo.getSuggestedAmountOfLoan());
			
			return processingDto;
		} else {
			return null;
		}
	}
	
	private UserDto convertCustomerEntityToOutputDto(Users user) {
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
	
	private SanctionInfo convertSanctionDtoToEntity(Long managerId, Long loanAppId, SanctionDto sanctionDto) {
		SanctionInfo sanctionInfo = new SanctionInfo();
		Double interestRate = 3.5;
		Double termPayentAmount = 0.0;
		Double monthlyPayment = 0.0;
		String loanClosureDate= "";
		
		sanctionInfo.setLoanAppId(loanAppId);
		sanctionInfo.setManagerId(managerId);
		sanctionInfo.setLoanAmountSanctioned(sanctionDto.getLoanAmountSanctioned());
		sanctionInfo.setTermOfLoan(sanctionDto.getTermOfLoan());
		sanctionInfo.setPaymentStartDate(sanctionDto.getPaymentStartDate());
		
		String startDateString = sanctionDto.getPaymentStartDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		//convert String to LocalDate
		LocalDate localDate = LocalDate.parse(startDateString, formatter);
		LocalDate closureDate = localDate.plusMonths(Math.round(sanctionDto.getTermOfLoan()));
		
		loanClosureDate = closureDate.format(formatter);
		termPayentAmount = ((sanctionDto.getLoanAmountSanctioned()) * (Math.pow((1 + interestRate/100), sanctionDto.getTermOfLoan())));
		monthlyPayment = (termPayentAmount) / (sanctionDto.getTermOfLoan());
		
		sanctionInfo.setLoanClosureDate(loanClosureDate);
		sanctionInfo.setMonthlyPayment(monthlyPayment);
		
		return sanctionInfo;
	}
	
	private SanctionOutputDto convertSanctionInfoToOutputDto(SanctionInfo sanctionInfo) {
		if (sanctionInfo!=null) {
			SanctionOutputDto sanctionOutputDto = new SanctionOutputDto();
			
			sanctionOutputDto.setLoanAmountSanctioned(sanctionInfo.getLoanAmountSanctioned());
			sanctionOutputDto.setTermOfLoan(sanctionInfo.getTermOfLoan());
			sanctionOutputDto.setPaymentStartDate(sanctionInfo.getPaymentStartDate());
			sanctionOutputDto.setLoanClosureDate(sanctionInfo.getLoanClosureDate());
			sanctionOutputDto.setMonthlyPayment(sanctionInfo.getMonthlyPayment());
			
			return sanctionOutputDto;
		} else {
			return null;
		}
	}

}

