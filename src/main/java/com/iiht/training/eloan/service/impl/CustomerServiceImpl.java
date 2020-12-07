package com.iiht.training.eloan.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.CustomerNotFoundException;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository; 
	
	@Autowired
	private ProcessingInfoRepository processingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	AdminServiceImpl adminServiceImp =new AdminServiceImpl();
	
	@Override
	public UserDto register(UserDto userDto) {
		Users customer = adminServiceImp.covertInputDtoToEntity(userDto);
		// save entity in DB : returns the copy of newly added record back
		Users newCustomer= this.usersRepository.save(customer);
		// convert entity into output dto
		UserDto customerOutputDto = adminServiceImp.convertEntityToUserOutputDto(newCustomer);
		return customerOutputDto;
	}

	@Override
	public LoanOutputDto applyLoan(Long customerId, LoanDto loanDto) {
		
		Loan loan = this.covertInputLoanDtoToEntity(loanDto);
		loan.setCustomerId(customerId);
		loan.setStatus(0);
		loan.setRemark("Loan Applied");
		// save entity in DB : returns the copy of newly added record back
		Loan newLoan= this.loanRepository.save(loan);
		// convert entity into output dto
		LoanOutputDto LoanOutputDto = this.convertEntityToLoanOutputDto(newLoan,customerId, loanDto);
		return LoanOutputDto;
	}

	@Override
	public LoanOutputDto getStatus(Long loanAppId) {
		Loan loan = this.loanRepository.findById(loanAppId).orElseThrow(() -> new LoanNotFoundException("Loan Not Found"));
		Long customerId = loan.getCustomerId();
		LoanDto loanDto =this.convertLoanEntityToOutputDto(loan);
		LoanOutputDto LoanOutputDto = this.convertEntityToLoanOutputDto(loan, customerId, loanDto);
		return LoanOutputDto;
	}
	
	
	private LoanDto convertLoanEntityToOutputDto(Loan loan) {
		LoanDto loanDto = new LoanDto();
			
		loanDto.setLoanName(loan.getLoanName());
		loanDto.setLoanAmount(loan.getLoanAmount());
		loanDto.setLoanApplicationDate(loan.getLoanApplicationDate());
		loanDto.setBusinessStructure(loan.getBusinessStructure());
		loanDto.setBillingIndicator(loan.getBillingIndicator());
		loanDto.setTaxIndicator(loan.getTaxIndicator());
			
			return loanDto;
	}

	@Override
	public List<LoanOutputDto> getStatusAll(Long customerId) {
		List<Loan> loans = this.loanRepository.findAllByCustomerId(customerId);
			
		List<LoanOutputDto> loanOutputDto = new ArrayList<LoanOutputDto>();
		for (Loan loan : loans) {
			Users user = this.usersRepository.findById(loan.getCustomerId())
					.orElseThrow(() -> new CustomerNotFoundException("Customer not Found"));
			UserDto userDto = this.convertUserEntityUserToDto(user);
			LoanDto loanDto = this.convertEntityToLoanDto(loan);
			LoanOutputDto loanOutDto = this.convertLoanEntityToLoanOutputDto(loan, userDto, loanDto);
			loanOutputDto.add(loanOutDto);
		}
		return loanOutputDto;
		
	}
	
	
	// utility method
	protected LoanOutputDto convertEntityToLoanOutputDto(Loan loan, Long customerId, LoanDto loanDto) {
		LoanOutputDto loanOutDto = new LoanOutputDto();

		loanOutDto.setCustomerId(loan.getCustomerId());
		loanOutDto.setLoanAppId(loan.getId());
		loanOutDto.setStatus(String.valueOf(loan.getStatus()));
		loanOutDto.setRemark(loan.getRemark());

		Users user = this.usersRepository.findByIdAndRole(customerId, "Customer")
				.orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));
		UserDto usrDto = this.convertCustomerEntityToOutputDto(user);
		loanOutDto.setLoanDto(loanDto);
		loanOutDto.setUserDto(usrDto);

		return loanOutDto;
	}
	
	protected Loan covertInputLoanDtoToEntity(LoanDto loanDto) {
			Loan loan = new Loan();
			loan.setLoanName(loanDto.getLoanName());
			loan.setLoanAmount(loanDto.getLoanAmount());
			loan.setLoanApplicationDate(loanDto.getLoanApplicationDate());
			loan.setBusinessStructure(loanDto.getBusinessStructure());
			loan.setBillingIndicator(loanDto.getBillingIndicator());
			loan.setTaxIndicator(loanDto.getTaxIndicator());
			
			return loan;
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
		
		

}
