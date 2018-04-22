package com.tools.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.hibernate.collection.internal.PersistentBag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.tools.model.Flight;
import com.tools.model.Passenger;
import com.tools.model.Plane;
import com.tools.model.Reservation;
import com.tools.responseParam.ErrorRequest;
import com.tools.responseParam.ErrorRequestMessage;
import com.tools.responseParam.FlightResponseParameters;
import com.tools.responseParam.SuccessRequest;

public class Helper {
	
	public ErrorRequest getErrorReponse(int code , String msg) {
		ErrorRequestMessage errorResponse = new ErrorRequestMessage();
		errorResponse.setCode(code);
		errorResponse.setMsg(msg);
		ErrorRequest errorRequest = new ErrorRequest();
		errorRequest.setBadRequest(errorResponse);
		return errorRequest ; 
	}
	
	public SuccessRequest getSuccessReponse(int code , String msg) {
		ErrorRequestMessage errorResponse = new ErrorRequestMessage();
		SuccessRequest success = new SuccessRequest();
		errorResponse.setCode(code);
		errorResponse.setMsg(msg);
		success.setSuccess(errorResponse);
		return success ; 
	}
	
	public boolean compareFlightOverlap(Date date1Start , Date date1End , Date date2Start , Date date2End) {
		if(date1Start.compareTo(date2End) == 0 && date1End.compareTo(date2End) == 1)return true; 
		if(date1Start.compareTo(date2Start) == -1 && date1End.compareTo(date2Start) == 0) return true ;
		if(date1Start.compareTo(date2Start) == 0 && date1End.compareTo(date2End)==0) return false;
		if(!compareDate(date1Start, date2End) && !compareDate(date1End, date2End)) return true ;
		if(compareDate(date1Start, date2Start) && compareDate(date1End, date2Start)) return true ;
		return false ;
	}
	
	public boolean compareDate(Date date1 , Date date2) {
		if(date1.compareTo(date2) == 1 ) return false ;
		return true ; 
	}
	
	public XStream objectToXML() {
		XStream xstream = new XStream();
		xstream.alias("Response", SuccessRequest.class);
		xstream.alias("passenger", Passenger.class);
		xstream.alias("reservation", Reservation.class);
		xstream.alias("flight", FlightResponseParameters.class);
		xstream.alias("flight", Flight.class);
		xstream.alias("Response", ErrorRequestMessage.class);
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.addDefaultImplementation(PersistentBag.class,List.class);
		xstream.addDefaultImplementation(PersistentBag.class,HashMap.class);
		xstream.addDefaultImplementation(PersistentBag.class,HashSet.class);
		xstream.omitField(Passenger.class, "success");
		xstream.omitField(Flight.class, "reservations");
		xstream.omitField(Reservation.class, "billing");
		xstream.omitField(Plane.class, "flight");
		xstream.omitField(Reservation.class, "passenger");
		xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream.getMapper()));
		return xstream;
	}
	
	public String convertToJSON(Object o)
	{
		ObjectMapper mapper = new ObjectMapper();
		String jsonString="";
		try {
			jsonString = mapper.writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			return jsonString;
		}
	}
	
}
