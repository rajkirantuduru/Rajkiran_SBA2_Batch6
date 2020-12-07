package com.iiht.training.eloan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.ExceptionResponse;
import com.iiht.training.eloan.exception.CustomerNotFoundException;
import com.iiht.training.eloan.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	

		@Autowired
		private CustomerService customerService;
		
		@PostMapping("/register")
		public ResponseEntity<UserDto> register(@RequestBody UserDto userDto){

			UserDto employeeOutputDto =  this.customerService.register(userDto);
			ResponseEntity<UserDto> response =
					new ResponseEntity<UserDto>(employeeOutputDto, HttpStatus.OK);
			
			return response;
		}
		
		@PostMapping("/apply-loan/{customerId}")
		public ResponseEntity<LoanOutputDto> applyLoan(@PathVariable Long customerId,
													 @RequestBody LoanDto loanDto){
			LoanOutputDto loanOutDto =  this.customerService.applyLoan(customerId, loanDto);
			ResponseEntity<LoanOutputDto> response =
					new ResponseEntity<LoanOutputDto>(loanOutDto, HttpStatus.OK);
			
			return response;
		}
		
		@GetMapping("/loan-status/{loanAppId}")
		public ResponseEntity<LoanOutputDto> getStatus(@PathVariable Long loanAppId){
			
			LoanOutputDto loanOutDto = this.customerService.getStatus(loanAppId);
			ResponseEntity<LoanOutputDto> response =
					new ResponseEntity<LoanOutputDto>(loanOutDto, HttpStatus.OK);
			
			return response;
		}
		
		@GetMapping("/loan-status-all/{customerId}")
		public ResponseEntity<List<LoanOutputDto>> getStatusAll(@PathVariable Long customerId){
			List<LoanOutputDto> loanOutDto = this.customerService.getStatusAll(customerId);
			ResponseEntity<List<LoanOutputDto>> response =
					new ResponseEntity<List<LoanOutputDto>>(loanOutDto, HttpStatus.OK);
			
			return response;
		}
		
		@ExceptionHandler(CustomerNotFoundException.class)
		public ResponseEntity<ExceptionResponse> handler(CustomerNotFoundException ex){
			ExceptionResponse exception = 
					new ExceptionResponse(ex.getMessage(),
										  System.currentTimeMillis(),
										  HttpStatus.NOT_FOUND.value());
			ResponseEntity<ExceptionResponse> response =
					new ResponseEntity<ExceptionResponse>(exception, HttpStatus.NOT_FOUND);
			return response;
		}
	}

