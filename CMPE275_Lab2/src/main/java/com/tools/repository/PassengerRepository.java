package com.tools.repository;

import org.springframework.stereotype.Repository;

import com.tools.model.Passenger;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger,Integer> {
	List<Passenger> findByPhone(String phone) ;

	List<Passenger> findById(int id) ;
}
