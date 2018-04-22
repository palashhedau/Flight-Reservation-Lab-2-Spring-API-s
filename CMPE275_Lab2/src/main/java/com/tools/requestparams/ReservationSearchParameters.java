package com.tools.requestparams;

public class ReservationSearchParameters {
	
	int passengerId;
	String origin;
	String to;
	String flightNumber;
	
	public int getPassengerId() {
		return passengerId;
	}
	public void setPassengerId(int passengerId) {
		this.passengerId = passengerId;
	}
	public String getOrigin() {
		return origin;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

}
