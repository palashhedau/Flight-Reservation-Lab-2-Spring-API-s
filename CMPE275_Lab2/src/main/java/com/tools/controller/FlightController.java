package com.tools.controller;

import java.util.HashMap;

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
import com.tools.model.Flight;
import com.tools.repository.FlightRepository;
import com.tools.requestparams.FlightParameters;
import com.tools.responseParam.ErrorRequest;
import com.tools.responseParam.FlightResponseParameters;
import com.tools.responseParam.SuccessRequest;
import com.tools.service.FlightService;

@RestController
public class FlightController {
	
	@Autowired
	FlightService flightService;
	
	HttpHeaders headers=new HttpHeaders();
	Helper helper = new Helper() ;
	
	@RequestMapping(path="/flight/{flightNumber}",method=RequestMethod.POST)
	public ResponseEntity<?> add(@PathVariable("flightNumber") String flightNumber , FlightParameters params){
		HashMap<String, FlightResponseParameters> flight = flightService.add(params, flightNumber );
		if(flight != null) return new  ResponseEntity(flight , HttpStatus.OK) ;
		else {
			ErrorRequest request = helper.getErrorReponse(400, "Flight exist with similar number or Please check arrival and departure time") ;
			return new  ResponseEntity(request , HttpStatus.BAD_REQUEST) ;
		} 
	}
	
	
	@RequestMapping(path="/flight/{flightNumber}",method=RequestMethod.PUT)
	public ResponseEntity<?> update(@PathVariable("flightNumber") String flightNumber , FlightParameters params){
		HashMap<String, FlightResponseParameters> flight = flightService.update(params, flightNumber );
		if(flight != null) return new  ResponseEntity(flight , HttpStatus.OK) ;
		else {
			ErrorRequest request = helper.getErrorReponse(404, "Flight does not exist with flight number " + flightNumber + " or Please check arrival and departure time") ;
			return new  ResponseEntity(request , HttpStatus.BAD_REQUEST) ;
		} 
	}
	
	
	@RequestMapping(path="/flight/{number}",method=RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("number") String flightNumber ){
		Boolean  Response  = flightService.delete(flightNumber );
		HttpHeaders headers = new HttpHeaders();
		if(Response) {
			
			SuccessRequest successRequest = helper.getSuccessReponse(200, "Flight with id "+flightNumber+" is deleted successfully.") ;
			
			String result_xml=helper.convertToJSON(successRequest);
			String _xml="";
			try {
				JSONObject json = new JSONObject(result_xml);
				_xml = XML.toString(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				headers.setContentType(MediaType.APPLICATION_XML);
				return new ResponseEntity<>(_xml,headers, HttpStatus.OK);
			}
		}else {
			ErrorRequest response = helper.getErrorReponse(404, "Flight with number "+flightNumber+" does not exist or flight has reservations") ;
			return new  ResponseEntity(response , headers ,  HttpStatus.NOT_FOUND) ;
		}
	}
	
	
	
	@RequestMapping(path="/flight/{flightNumber}",method=RequestMethod.GET)
	public ResponseEntity<?> get(@PathVariable("flightNumber") String flightNumber,@RequestParam(value="xml", required=false) boolean xml ){
		HashMap<String, FlightResponseParameters> params = flightService.get(flightNumber) ;
		if(params != null ) {
			if(xml==true)
			{
				String result_xml=helper.convertToJSON(params);
				String _xml="";
				try {
					JSONObject json = new JSONObject(result_xml);
					_xml = XML.toString(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					headers.setContentType(MediaType.APPLICATION_XML);
					return new ResponseEntity<>(_xml,headers, HttpStatus.OK);
				}
			}
			else return new ResponseEntity( params , HttpStatus.OK) ; 
		}else {
			ErrorRequest response = helper.getErrorReponse(404, "Sorry, the request flight with number " + flightNumber + " does not exist") ;
			return new ResponseEntity( response , HttpStatus.NOT_FOUND) ; 
		}
	}
}
