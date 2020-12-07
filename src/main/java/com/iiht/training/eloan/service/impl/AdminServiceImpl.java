package com.iiht.training.eloan.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UsersRepository usersRepository;

	@Override
	public UserDto registerClerk(UserDto userDto) {
		// TODO Auto-generated method stub
		Users employee = this.covertInputDtoToEntity(userDto);
		// save entity in DB : returns the copy of newly added record back
		Users newEmployee = this.usersRepository.save(employee);
		// convert entity into output dto
		UserDto employeeOutputDto = this.convertEntityToUserOutputDto(newEmployee);
		return employeeOutputDto;

	}

	@Override
	public UserDto registerManager(UserDto userDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserDto> getAllClerks() {
		List<Users> Clerks = this.usersRepository.findByRole("Clerk");
		
		List<UserDto> UserDto = new ArrayList<UserDto>(); 
		for(Users clerk : Clerks) {
			UserDto userDto = this.convertEntityToUserOutputDto(clerk);
			UserDto.add(userDto);
		}
		return UserDto;
	}

	@Override
	public List<UserDto> getAllManagers() {
		List<Users> Managers = this.usersRepository.findByRole("Manager");

		List<UserDto> UserDto = new ArrayList<UserDto>();
		for (Users Manager : Managers) {
			UserDto userDto = this.convertEntityToUserOutputDto(Manager);
			UserDto.add(userDto);
		}
		return UserDto;
	}

	// utility method
	protected UserDto convertEntityToUserOutputDto(Users employee) {
		UserDto employeeOutputDto = new UserDto();
		employeeOutputDto.setId(employee.getId());
		employeeOutputDto.setFirstName(employee.getFirstName());
		employeeOutputDto.setLastName(employee.getLastName());
		employeeOutputDto.setEmail(employee.getEmail());
		employeeOutputDto.setMobile(employee.getMobile());
		
		return employeeOutputDto;
	}

	protected Users covertInputDtoToEntity(UserDto employeeInputDto) {
		Users employee = new Users();
		employee.setId(employeeInputDto.getId());
		employee.setFirstName(employeeInputDto.getFirstName());
		employee.setLastName(employeeInputDto.getLastName());
		employee.setEmail(employeeInputDto.getEmail());
		employee.setMobile(employeeInputDto.getMobile());
		
		return employee;
	}
}
