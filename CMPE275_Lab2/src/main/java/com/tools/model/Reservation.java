package com.tools.model;

import java.util.HashMap;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Reservation")
public class Reservation {
	
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id
	int reservationNumber;

	@JsonIgnore
	int passengerId;
	
	@JsonIgnore
	int billingId;
	
	@ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE
            })
    @JoinTable(name = "reservations_flights",
            joinColumns = { @JoinColumn(name = "reservationNumber") },
            inverseJoinColumns = { @JoinColumn(name = "number") })
    private List<Flight> flights;
	
	@Transient
	public int getPrice() {
		return this.billing.getCost();
	}

	@JsonIgnore
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="passengerId", insertable=false ,  updatable=false)
	Passenger passenger; 
	
	
	
	@ManyToOne(fetch=FetchType.EAGER ,  cascade = CascadeType.ALL)
	@JoinColumn(name="billingId", insertable=false ,  updatable=false )
	Billing billing; 
	
	
	
	
	
	public Reservation(int reservationNumber, int passengerId, int billingId, List<Flight> flights, Passenger passenger,
			Billing billing) {
		super();
		this.reservationNumber = reservationNumber;
		this.passengerId = passengerId;
		this.billingId = billingId;
		this.flights = flights;
		this.passenger = passenger;
		this.billing = billing;
	}
	
	public Reservation() {
		// TODO Auto-generated constructor stub
	}

	public HashMap<String, List<Flight>>  getFlights() {
		HashMap<String, List<Flight>> hm = new HashMap<String, List<Flight>>();
		hm.put("flight", flights);
		return hm;
	}
	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}
	public int getReservationNumber() {
		return reservationNumber;
	}
	public void setReservationNumber(int reservationNumber) {
		this.reservationNumber = reservationNumber;
	}
	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}
	public List<Flight> fetchFlights() {
		return flights;
	}
	public void setBilling(Billing billing) {
		this.billing = billing;
	}
	public int getPassengerId() {
		return passengerId;
	}
	public void setPassengerId(int passengerId) {
		this.passengerId = passengerId;
	}
	public int getBillingId() {
		return billingId;
	}
	public Passenger getPassenger() {
		return passenger;
	}
	public void setBillingId(int billingId) {
		this.billingId = billingId;
	}
	

}
