package com.tools.responseParam;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.tools.model.Plane;

public class FlightResponseParameters {
	
	
	
	String number ;
	String price ;
	String origin ;
	String destination ;
	Date departureTime ;
	Date arrivalTime ;
	String description ;
	int planeNumber ;
	int seatsLeft;
	
	Plane plane ;
	HashMap<String, List<FlightResponse>> passengers ;
	
	
	
	public HashMap<String, List<FlightResponse>> getPassengers() {
		return passengers;
	}
	public void setPassenger(HashMap<String, List<FlightResponse>> passengers) {
		this.passengers = passengers;
	}
	public FlightResponseParameters( String number, String price, String origin,
			String destination, Date departureTime, Date arrivalTime, String description, int planeNumber,
			int seatsLeft , Plane plane) {
		super();
		this.number = number;
		this.price = price;
		this.origin = origin;
		this.destination = destination;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.description = description;
		this.planeNumber = planeNumber;
		this.seatsLeft = seatsLeft;
		this.plane = plane;
	}
	public String getNumber() {
		return number;
	}
	public Date getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getDescription() {
		return description;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getPrice() {
		return price;
	}
	public Date getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPlaneNumber() {
		return planeNumber;
	}
	public void setPlaneNumber(int planeNumber) {
		this.planeNumber = planeNumber;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public int getSeatsLeft() {
		return seatsLeft;
	}
	public void setSeatsLeft(int seatsLeft) {
		this.seatsLeft = seatsLeft;
	}
	public Plane getPlane() {
		return plane;
	}
	public void setPlane(Plane plane) {
		this.plane = plane;
	}
	
	
}
