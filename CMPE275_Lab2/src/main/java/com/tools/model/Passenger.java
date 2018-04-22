package com.tools.model;

import java.util.*;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="Passenger")
public class Passenger {
	
	@Id
	@GeneratedValue
	public int id ;
	@Column(nullable=false)
	private String firstname;
	@Column(nullable=false)
	private String lastname;
	@Column(nullable=false)
	private int age;
	@Column(nullable=false)
	private String gender;
	@Column(nullable=false,unique=true)
	private String phone; // Phone numbers must be unique
	@OneToMany(mappedBy="passenger", cascade=CascadeType.ALL)
	private List<Reservation> reservations ;

	
	
	public Passenger(String firstname, String lastname, int age, String gender, String phone){
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.age = age;
		this.gender = gender;
		this.phone = phone;
	}
	
	public Passenger() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public HashMap<String, List<Reservation>>  getReservations() {
		HashMap<String, List<Reservation>> hm = new HashMap<String, List<Reservation>>();  
		hm.put("reservation", reservations);
		return hm;
	}
	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	public String getFirstname() {
		return firstname;
	}
	public int getAge() {
		return age;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
