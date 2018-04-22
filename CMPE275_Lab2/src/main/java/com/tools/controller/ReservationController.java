package com.tools.controller;

import java.util.HashMap;
import java.util.List;

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
import com.tools.helper.Helper;
import com.tools.model.Reservation;
import com.tools.requestparams.ReservationSearchParameters;
import com.tools.requestparams.ReservationUpdateParameters;
import com.tools.responseParam.ErrorRequest;
import com.tools.responseParam.ReservationResponse;
import com.tools.responseParam.SuccessRequest;
import com.tools.service.ReservationService;



@RestController
public class ReservationController {
	
	@Autowired
	ReservationService reservationService;
	
	Helper helper = new Helper() ;
	HttpHeaders headers=new HttpHeaders();
	
	@RequestMapping(path="/reservation",method=RequestMethod.POST)
	public ResponseEntity<?> add(@RequestParam("passengerId") String passengerId ,
			@RequestParam("flightLists") String flightLists){
		
		ReservationResponse response =  reservationService.add(passengerId, flightLists );
		if(response != null ) {
			String jsonObject=helper.convertToJSON(response);
			String xmlString="";
			try {
				JSONObject json = new JSONObject(jsonObject);
				xmlString = XML.toString(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				headers.setContentType(MediaType.APPLICATION_XML);
				return new ResponseEntity<>(xmlString,headers, HttpStatus.OK);
			}
		}else {
			ErrorRequest errorResponse = helper.getErrorReponse(400, "Either Passenger with ID " + passengerId + " does not exist or check flights overlapping/existance");
			return new ResponseEntity( errorResponse , HttpStatus.BAD_REQUEST) ;
		}
		
	}
	

	@RequestMapping(path="/reservation/{number}",method=RequestMethod.PUT)
	public ResponseEntity<?> update(ReservationUpdateParameters params , @PathVariable("number") String number ){
		ReservationResponse response =  reservationService.update(params, number ) ; 
		if(response == null) {
			ErrorRequest request = helper.getErrorReponse(404, "Reservation with ID " + number + " does not exist or flights number to be add/remove does not exist");
			return new ResponseEntity( request , HttpStatus.NOT_FOUND) ;
		}else return new ResponseEntity(  response, HttpStatus.OK) ;
	}
	
	
	@RequestMapping(path="/reservation/{number}",method=RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("number") String number){
		Boolean result =  reservationService.delete(number);
		ErrorRequest request = helper.getErrorReponse(400, "Reservation with ID " + number + " does not exist");
		if(result) {
			SuccessRequest successRequest = helper.getSuccessReponse(200, "Reservation with ID "+number+" is deleted successfully.") ;
			XStream xStream=helper.objectToXML();	
			headers.setContentType(MediaType.APPLICATION_XML);
			return new ResponseEntity<>(xStream.toXML(successRequest.getSuccess()),headers, HttpStatus.OK);
		}else return new ResponseEntity( request , HttpStatus.BAD_REQUEST) ;
	}
	
	
	@RequestMapping(path="/reservation",method=RequestMethod.GET)
	public ResponseEntity<?> search(ReservationSearchParameters params){
		List<Reservation> result =  reservationService.get(params.getPassengerId(),params.getOrigin(),params.getTo(),params.getFlightNumber());
		
		if(result !=null) {
			
			HashMap<String, List<Reservation>> map = new HashMap<>();
			map.put("Reservation", result);
			HashMap<String, HashMap<String,List<Reservation>>> map2 = new HashMap<>();
			map2.put("Reservations", map);
			
			String _xml = "";
			try {
				String json = helper.convertToJSON(map2);
				System.out.println(json);
				JSONObject jsonObj = new JSONObject(json);
				_xml = XML.toString(jsonObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			headers.setContentType(MediaType.APPLICATION_XML);
			return new ResponseEntity( _xml , headers , HttpStatus.OK) ;
		}
		else {
			ErrorRequest response = helper.getErrorReponse(400, "No Reservation found");
			return new ResponseEntity( response , HttpStatus.BAD_REQUEST) ;
		}	
	}
	
	
	@RequestMapping(path="/reservation/{number}",method=RequestMethod.GET)
	public ResponseEntity<?> get(@PathVariable("number") String number){
		ReservationResponse result =  reservationService.getByNumber(number);
		if(result==null) {
			ErrorRequest response = helper.getErrorReponse(404, "Reservation with number " + number + " does not exist");
			return new ResponseEntity( response , HttpStatus.NOT_FOUND) ;
		}else return new ResponseEntity(  result , HttpStatus.OK) ;
	}
	
	
	
}
