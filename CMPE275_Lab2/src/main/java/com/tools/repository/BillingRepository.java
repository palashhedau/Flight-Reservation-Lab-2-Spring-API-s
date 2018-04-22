package com.tools.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tools.model.Billing;

@Repository
public interface BillingRepository extends JpaRepository<Billing,Integer> {
	List<Billing> findById(int id );
}
