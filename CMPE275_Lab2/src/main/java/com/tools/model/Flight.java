package com.tools.model;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Flight")
public class Flight {
	@Id
	String number ;
	String price ;
	String origin ;
	String destination ;
	Date departureTime ;
	Date arrivalTime ;
	String description ;
	int planeNumber ;
	int seatsLeft;
	
	@ManyToMany(fetch = FetchType.LAZY,cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "flights")
    private List<Reservation> reservations;
	
	@OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="planeNumber", insertable=false ,  updatable=false)
	private Plane plane;
	
	@JsonIgnore
	public List<Reservation> getReservations() {
		return reservations;
	}
	
	public Flight(String number, String price, String origin, String destination, Date departureTime, Date arrivalTime,
			String description, int planeNumber, int seatsLeft) {
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
	}



	public Flight() {
		// TODO Auto-generated constructor stub
	}



	public int getSeatsLeft() {
		return seatsLeft;
	}
	public void setSeatsLeft(int seatsLeft) {
		this.seatsLeft = seatsLeft;
	}

	public int getPlaneNumber() {
		return planeNumber;
	}
	public void setPlaneNumber(int planeNumber) {
		this.planeNumber = planeNumber;
	}
	public Plane getPlane() {
		return plane;
	}
	public void setPlane(Plane plane) {
		this.plane = plane;
	}
	
	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getPrice() {
		return price;
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
	public Date getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
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
	public void setDescription(String description) {
		this.description = description;
	}
}
