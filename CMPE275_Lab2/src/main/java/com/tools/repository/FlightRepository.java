package com.tools.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tools.model.Flight;

@Repository
public interface FlightRepository extends JpaRepository<Flight,String> {
	List<Flight> findByNumber(String number) ; 
}
