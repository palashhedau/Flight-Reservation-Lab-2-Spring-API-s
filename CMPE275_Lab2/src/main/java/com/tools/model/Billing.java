package com.tools.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Billing")
public class Billing {
	@Id
	@GeneratedValue
	int id ;
	int cost;
	@OneToMany(mappedBy="billing", cascade=CascadeType.ALL)
	private List<Reservation> reservation ;
	
	
	public Billing(int cost, List<Reservation> reservation) {
		super();
		this.cost = cost;
		this.reservation = reservation;
	}
	public Billing() {
		// TODO Auto-generated constructor stub
	}
	@JsonIgnore
	public List<Reservation> getReservation() {
		return reservation;
	}
	public void setReservation(List<Reservation> reservation) {
		this.reservation = reservation;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
}
