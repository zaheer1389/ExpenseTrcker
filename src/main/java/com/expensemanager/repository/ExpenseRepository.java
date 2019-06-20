package com.expensemanager.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.expensemanager.dao.Expense;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {
	
	@Query(value = "SELECT * FROM expense WHERE Date between :fromDate AND :toDate", nativeQuery =  true)
	List<Expense> findBetweenDate(@Param("fromDate")Date fromDate, @Param("toDate")Date toDate);
	
	@Query(value = "SELECT SUM(Amount) FROM expense WHERE Date < :fromDate", nativeQuery =  true)
	Double getOpeningBalance(@Param("fromDate")Date fromDate);
	
}
