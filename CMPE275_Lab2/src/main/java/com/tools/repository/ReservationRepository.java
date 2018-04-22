package com.tools.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tools.model.Flight;
import com.tools.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,String> {
	List<Reservation> findByBillingId(int billingId) ;
	
	@Query("select DISTINCT(R)"
			+ " from Reservation R"
			+ " JOIN R.flights F"
			+ " JOIN R.passenger P"
			+ " where ((:flightNumber IS NULL) OR (:flightNumber IS NOT NULL AND F.number= :flightNumber))"
			+ " and ((:origin IS NULL) OR (:origin IS NOT NULL AND F.origin = :origin))"
			+ " and ((:destination IS NULL) OR (:destination IS NOT NULL AND F.destination = :destination))"
			+ " and ((:passengerId = 0 ) OR (:passengerId <> 0 AND P.id = :passengerId))")
	List<Reservation> serachReservation(@Param("passengerId")int passengerId, 
			@Param("origin") String origin,
			@Param("destination") String destination, 
			@Param("flightNumber")String flightNumber);
			
	List<Reservation> findByPassengerId(int passenger_id ) ;
	
	List<Reservation> findByFlightsNumber(String number); 
}
