package com.tools.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.hibernate.collection.internal.PersistentBag;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.tools.helper.Helper;
import com.tools.model.Flight;
import com.tools.model.Passenger;
import com.tools.model.Plane;
import com.tools.model.Reservation;
import com.tools.requestparams.PassengerParameters;
import com.tools.responseParam.ErrorRequest;
import com.tools.responseParam.ErrorRequestMessage;
import com.tools.responseParam.SuccessRequest;
import com.tools.service.PassengerService;

@RestController
public class PassengerController {
	
	@Autowired
	PassengerService passengerService;
	
	Helper helper = new Helper();
	HttpHeaders headers=new HttpHeaders();
	
	@RequestMapping(path="/passenger",method=RequestMethod.POST)
	public ResponseEntity<?> add(PassengerParameters params){
		List<Passenger> passengerList = passengerService.add(params);
		if(passengerList == null) {
			ErrorRequest response = helper.getErrorReponse(400, "Another passenger with the same number already exists") ;
			return new  ResponseEntity(response , HttpStatus.BAD_REQUEST) ;
		}else {
			Passenger passenger = passengerList.get(0);
			HashMap<String, Passenger> map = new HashMap<>();
			map.put("passenger", passenger);
			return new  ResponseEntity(map , HttpStatus.OK) ;
		}
	}
	
	@RequestMapping(path="/passenger/{id}",method=RequestMethod.PUT)
	public ResponseEntity<?> update(PassengerParameters params , @PathVariable("id") String id)
	{
		Passenger passenger = passengerService.update(params , id);
		if(passenger == null) {
			ErrorRequest response = helper.getErrorReponse(404, "User does not exist or phone number already taken") ;
			return new  ResponseEntity(response , HttpStatus.NOT_FOUND) ;
		}else {
			HashMap<String, Passenger> map = new HashMap<>();
			map.put("passenger", passenger);
			return new  ResponseEntity(map , HttpStatus.OK) ;
		}
	}
	
	
	@RequestMapping(path="/passenger/{id}",method=RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("id") String id)
	{
		Boolean success = passengerService.delete(id);
		if(success) {
			XStream xStream=helper.objectToXML();	
			headers.setContentType(MediaType.APPLICATION_XML);
			SuccessRequest response = helper.getSuccessReponse(200, "Passenger with id "+id+" is deleted successfully.") ;
			return new ResponseEntity<>(xStream.toXML(response.getSuccess()),headers, HttpStatus.OK);			
		}else {
			ErrorRequest response = helper.getErrorReponse(404, "Passenger with id "+id+" does not exist") ;
			return new ResponseEntity(response , HttpStatus.NOT_FOUND) ;
		}
	}
	
	
	@RequestMapping(path="/passenger/{id}", method=RequestMethod.GET)
	public ResponseEntity<?> get(@PathVariable("id") String id,@RequestParam(value="xml", required=false) boolean xml) 
	{
		HttpHeaders headers = new HttpHeaders();
		List<Passenger> passenger = passengerService.get(id) ; 
		if(passenger.size() > 0) {
			HashMap<String, Passenger> map = new HashMap<>();
			map.put("passenger", passenger.get(0));
			if(xml==true)
			{
				String obj = helper.convertToJSON(map);
				String xmlObject="";
				try {
					JSONObject json = new JSONObject(obj);
					xmlObject = XML.toString(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					headers.setContentType(MediaType.APPLICATION_XML);
					return new ResponseEntity<>(xmlObject,headers, HttpStatus.OK);
				}
			}
			else return new ResponseEntity<>(map, HttpStatus.OK);
		} else {
			ErrorRequest response = helper.getErrorReponse(404, "Sorry, the requested passenger with id " +id+ " does not exist");
			return new ResponseEntity<>(response , HttpStatus.NOT_FOUND);
		}
	}
}
