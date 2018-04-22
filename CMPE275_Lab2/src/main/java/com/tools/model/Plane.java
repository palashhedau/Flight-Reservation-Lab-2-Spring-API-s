package com.tools.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Plane")
public class Plane {
	
	private int capacity;
	private String model;
	private String manufacturer;
	private String year;

	@JsonIgnore
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	private int id;
	
	@OneToOne(mappedBy="plane", cascade=CascadeType.ALL)
	Flight flight; 

	public Plane(int capacity, String model, String manufacturer, String year) {
		this.capacity = capacity;
		this.model = model;
		this.manufacturer = manufacturer;
		this.year = year;
	}
	
	public Plane() {}
	
	
	public String getManufacturer() {
		return manufacturer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	
	
}
