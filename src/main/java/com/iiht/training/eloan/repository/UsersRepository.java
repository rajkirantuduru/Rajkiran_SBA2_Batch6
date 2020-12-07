package com.iiht.training.eloan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iiht.training.eloan.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>{
	
	List<Users> findByRole(String role);
	List<Users> findById(String id);
	Optional<Users> findByIdAndRole(Long id,String role);

}
