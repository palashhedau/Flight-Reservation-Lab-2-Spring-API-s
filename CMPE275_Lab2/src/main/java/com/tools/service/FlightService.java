package com.tools.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tools.helper.Helper;
import com.tools.model.Flight;
import com.tools.model.Passenger;
import com.tools.model.Plane;
import com.tools.model.Reservation;
import com.tools.repository.FlightRepository;
import com.tools.repository.PlaneRepository;
import com.tools.repository.ReservationRepository;
import com.tools.requestparams.FlightParameters;
import com.tools.responseParam.FlightResponse;
import com.tools.responseParam.FlightResponseParameters;


@Transactional(isolation=Isolation.READ_COMMITTED,propagation=Propagation.REQUIRED,rollbackFor=Exception.class,timeout=10)
@Service
public class FlightService {
	
	Helper helper = new Helper();
	
	@Autowired
	private PlaneRepository planeRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private FlightRepository flightRepository;
	
	
	public HashMap<String, FlightResponseParameters> add(FlightParameters params , String number)
	{
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
			sdf.setTimeZone(TimeZone.getTimeZone("PST"));
			
			Date departure = sdf.parse(params.getDepartureTime());
			Date arrival = sdf.parse(params.getArrivalTime());
			
			if( !(arrival.compareTo(departure) == 1))return null; 
			
			
			Flight flight = transformToModel(params , number);
			flight =  flightRepository.findByNumber(number).size() == 0 ? flightRepository.save(flight ) : null ;
			if(flight != null) return get(number);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	

	public HashMap<String, FlightResponseParameters> update(FlightParameters ps , String number)
	{
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
			sdf.setTimeZone(TimeZone.getTimeZone("PST"));
			
			Date departure1 = sdf.parse(ps.getDepartureTime());
			Date arrival1 = sdf.parse(ps.getArrivalTime());
			
			if( !(arrival1.compareTo(departure1) == 1))return null; 
			
			
			
			if(flightRepository.findByNumber(number).size() > 0) {
				
				Flight flightToUpdate = flightRepository.findByNumber(number).get(0);
				Plane plane = planeRepository.findById(flightToUpdate.getPlaneNumber()).get(0);
				
				int seatLeft = flightToUpdate.getSeatsLeft();
				int capacity = plane.getCapacity() ;
				int reserved = capacity-seatLeft ;
				
				if(ps.getCapacity() < 1) return null ; 
				if(ps.getCapacity() <= reserved) return null ; 
				
				Date date1Start = departure1;
				Date date1End = arrival1;
				
				Date date2Start = flightToUpdate.getDepartureTime();
				Date date2End = flightToUpdate.getArrivalTime();
				
				//check flight overlapping if time changed
				if( !( date1Start.compareTo(date2Start) == 0 && date2Start.compareTo(date2End) == 0 )) {
					boolean timeConflict = false; 
					List<Reservation> reservations = reservationRepository.findAll() ;
					for(Reservation reservation : reservations) {
						List<Flight> flights = reservation.fetchFlights();
						for(Flight tempFlight : flights) {
							if(!tempFlight.getNumber().equalsIgnoreCase(number)) {
								date2Start = tempFlight.getDepartureTime();
								date2End = tempFlight.getArrivalTime();
								if (!helper.compareFlightOverlap(date1Start, date1End, date2Start, date2End)) {
									timeConflict = true;
									break;
								}
							}
						}
						if(timeConflict) break;
					}
					if(timeConflict) return null ;
				}
				
				
				//Changes to save in the updateFlight
				Date arrival = sdf.parse(ps.getArrivalTime());
				Date departure = sdf.parse(ps.getDepartureTime());
				int seatLeftToSave = ps.getCapacity()-reserved ;
				flightToUpdate.setSeatsLeft(seatLeftToSave);
				
				Plane p = flightToUpdate.getPlane() ;
				p.setCapacity(ps.getCapacity());
				p = planeRepository.save(plane);
				
				flightToUpdate.setPlaneNumber(p.getId());
				flightToUpdate.setNumber(number);
				flightToUpdate.setDescription(ps.getDescription());
				flightToUpdate.setOrigin(ps.getOrigin());
				flightToUpdate.setPrice(ps.getPrice());
				flightToUpdate.setDestination(ps.getDestination());
				flightToUpdate.setArrivalTime(arrival);
				flightToUpdate.setDepartureTime(departure);
				
				flightRepository.save(flightToUpdate);
				return get(number);
				
			}else return null ;
		} catch (Exception e) {
			e.printStackTrace();
			return null ;
		} 
	}
	
	public boolean delete(String flightNumber) {
		if(flightRepository.findByNumber(flightNumber).size() == 0) return false;
		else
		{
			List<Reservation> reservationList = reservationRepository.findByFlightsNumber(flightNumber);
			if(reservationList.size() > 0) return false;
			else {
				System.out.println("Delete krre " + flightNumber);
				flightRepository.delete(flightNumber);
				return true;
			}
		}  
	}
	
	public  HashMap<String, FlightResponseParameters> get(String flightNumber) {
		
		List<Flight> flights = flightRepository.findByNumber(flightNumber);	
		if(flights.size() > 0) {
			Flight flight = flights.get(0);
			FlightResponseParameters param = new FlightResponseParameters(flight.getNumber() , flight.getPrice(), flight.getOrigin(),
					flight.getDestination(), flight.getDepartureTime(), flight.getArrivalTime(),flight.getDescription(), flight.getPlaneNumber(),
					flight.getSeatsLeft() , flight.getPlane());
			
			List<Reservation> reservations = reservationRepository.findByFlightsNumber(flightNumber);
			HashMap<String,FlightResponseParameters> map = new HashMap<>();
			if(reservations.size() > 0) {
				List<FlightResponse> response = new ArrayList<>();
				for(Reservation reservation : reservations) {
					Passenger temp = reservation.getPassenger();
					FlightResponse pass = new FlightResponse(temp.getId(),temp.getFirstname(),
							temp.getLastname() , temp.getAge() , temp.getGender() , temp.getPhone());
					response.add(pass) ;
				}
				HashMap<String, List<FlightResponse>> resultMap = new HashMap<String, List<FlightResponse>>();  
				resultMap.put("passenger",response );
				param.setPassenger(resultMap);
			}
			map.put("flight", param);
			return map;
		}else return null ;
	}
	
	
	public Flight transformToModel(FlightParameters params , String number)  {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
		sdf.setTimeZone(TimeZone.getTimeZone("PST"));
		
		try {
			
			Plane plane = new Plane(params.getCapacity(), params.getModel(), params.getManufacturer(), params.getYear()) ;
			plane = planeRepository.save(plane);
			
			return new Flight( number, params.getPrice(), params.getOrigin(), 
					params.getDestination(), sdf.parse(params.getDepartureTime()), sdf.parse(params.getArrivalTime()),
					params.getDescription(), plane.getId(), params.getCapacity()) ;
		
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return null;
		
		
		
	}
}
