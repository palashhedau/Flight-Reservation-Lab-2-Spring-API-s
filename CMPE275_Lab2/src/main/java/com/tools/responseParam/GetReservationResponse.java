package com.tools.responseParam;

import java.util.HashMap;
import java.util.List;
import com.tools.model.Flight;

public class GetReservationResponse {
	HashMap<String, List<Flight>> flights ;
	PassengerParameters passenger ;
	int reservationNumber ;
	int price ; 
	
	public int getReservationNumber() {
		return reservationNumber;
	}
	public void setReservationNumber(int reservationNumber) {
		this.reservationNumber = reservationNumber;
	}
	public int getPrice() {
		return price;
	}
	
	public PassengerParameters getPassenger() {
		return passenger;
	}
	public void setPassenger(PassengerParameters passenger) {
		this.passenger = passenger;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	public HashMap<String, List<Flight>> getFlights() {
		return flights;
	}
	public void setFlights(HashMap<String, List<Flight>> flights) {
		this.flights = flights;
	}
	
	
}
