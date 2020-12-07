package com.iiht.training.eloan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iiht.training.eloan.entity.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>{

	@Modifying
	@Transactional
	@Query("update Loan e set e.remark = ?1, e.status = ?2 where e.id = ?3")
	void setStatusRemark(String remark, Integer status, Long id);
	
	List<Loan> findAllByCustomerId(Long customerId);
	
	Optional<List<Loan>> findAllByStatus(Integer status);
	
	@Modifying
	@Transactional
	@Query("update Loan e set e.status = ?1 where e.id = ?2") 
	void setStatusForLoan(Integer status, Long id);
}
