package com.tools.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.tools.model.Flight;
import com.tools.model.Passenger;
import com.tools.model.Reservation;
import com.tools.repository.FlightRepository;
import com.tools.repository.PassengerRepository;
import com.tools.repository.ReservationRepository;
import com.tools.requestparams.PassengerParameters;

@Transactional(isolation=Isolation.READ_COMMITTED,propagation=Propagation.REQUIRED,rollbackFor=Exception.class,timeout=10)
@Service
public class PassengerService {
	
	@Autowired
	private PassengerRepository passengerRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private FlightRepository flightRepository;
	
	public List<Passenger> add(PassengerParameters params)
	{
		Passenger psg = transformToModel(params);
		psg = passengerRepository.findByPhone(psg.getPhone()).size() == 0 ? passengerRepository.save(psg ) : null ; 
		if(psg == null) return null ;
		return get(Integer.toString(psg.getId()));
	}
	
	public Passenger update(PassengerParameters params , String id)
	{
		if( passengerRepository.findById(Integer.parseInt(id)).size() ==  0) 
			return null;
		
		Passenger savedPassenger = passengerRepository.findById(Integer.parseInt(id)).get(0) ;
		
		if(!params.getPhone().equalsIgnoreCase(savedPassenger.getPhone())) 
			if(passengerRepository.findByPhone(params.getPhone()).size() > 0) return null;
		
		savedPassenger.setGender(params.getGender());
		savedPassenger.setLastname(params.getLastname());
		savedPassenger.setPhone(params.getPhone());
		savedPassenger.setAge(params.getAge());
		savedPassenger.setFirstname(params.getFirstname());
		return passengerRepository.save(savedPassenger); 
	}
	

	public boolean delete(String id)
	{
		if(passengerRepository.findById(Integer.parseInt( id)).size() == 0) return false;
		else
		{
			List<Reservation> reservationList = reservationRepository.findByPassengerId(Integer.parseInt(id));
			for(Reservation reservation : reservationList) {
				List <Flight> flightList = reservation.fetchFlights() ;
				for(Flight flight : flightList) {
					flight.setSeatsLeft(flight.getSeatsLeft() + 1);
					flightRepository.save(flight); 
				}
			}
			passengerRepository.delete(Integer.parseInt( id));
			return true;
		}  
	}
	
	public List<Passenger> get(String id) {
		return passengerRepository.findById(Integer.parseInt(id));
	}
	
	public Passenger transformToModel(PassengerParameters params) {
		return new Passenger(params.getFirstname(), params.getLastname(),params.getAge(),
				params.getGender(), params.getPhone()) ;
	}
	
}
