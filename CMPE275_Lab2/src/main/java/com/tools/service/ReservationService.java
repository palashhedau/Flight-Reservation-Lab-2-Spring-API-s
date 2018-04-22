package com.tools.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tools.helper.Helper;
import com.tools.model.Billing;
import com.tools.model.Flight;
import com.tools.model.Passenger;
import com.tools.model.Reservation;
import com.tools.repository.BillingRepository;
import com.tools.repository.FlightRepository;
import com.tools.repository.PassengerRepository;
import com.tools.repository.ReservationRepository;
import com.tools.requestparams.ReservationUpdateParameters;
import com.tools.responseParam.GetReservationResponse;
import com.tools.responseParam.PassengerParameters;
import com.tools.responseParam.ReservationResponse;

@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.REQUIRED,rollbackFor=Exception.class,timeout=10)
@Service
public class ReservationService {

	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private BillingRepository billingRepository;
	@Autowired
	private FlightRepository flightRepository;
	@Autowired
	private PassengerRepository passengerRepository;

	Helper helper = new Helper();

	public ReservationResponse add(String passengerId, String flightLists) {

		List<Passenger> passengers = passengerRepository.findById(Integer.parseInt(passengerId));
		if (passengers.size() > 0) {
			List<String> flights = Arrays.asList(flightLists.split(","));

			int cost = 0;
			List<Reservation> reservationResult = reservationRepository.findByPassengerId(Integer.parseInt(passengerId));

			List<String> flightToAdd = new ArrayList<>();
			for (String flightNumber : flights) {
				boolean remove = false;
				for (Reservation res : reservationResult) {
					for (Flight flight : res.fetchFlights()) {
						if (flight.getNumber().equalsIgnoreCase(flightNumber)) {
							remove = true;
							break;
						}
					}
					if (remove == true)
						break;
				}
				if (remove == false)
					flightToAdd.add(flightNumber);
			}

			List<String> anotherFlightToAdd = new ArrayList<>();
			for (String flightNumber : flightToAdd) {
				List<Flight> flightList = flightRepository.findByNumber(flightNumber);
				if (flightList.size() > 0)
					anotherFlightToAdd.add(flightNumber);
			}

			if (anotherFlightToAdd.size() > 0) {
				ArrayList<Flight> flightSet = new ArrayList<>();
				Reservation rs = new Reservation();
				
				for (String flightNumber : anotherFlightToAdd) {
					
					List<Flight> flightList = flightRepository.findByNumber(flightNumber);
					Flight currentFlight = flightList.get(0);
					boolean overlap = false;
					
					for (Reservation res : reservationResult) {
						for (Flight flight : res.fetchFlights()) {
							Date date2End = flight.getArrivalTime();
							Date date2Start = flight.getDepartureTime();
							Date date1Start = currentFlight.getDepartureTime();
							Date date1End = currentFlight.getArrivalTime();
							if (!helper.compareFlightOverlap(date1Start, date1End, date2Start, date2End)) overlap = true; break;
						}
						if (overlap == true)break;
					}
					

					if (currentFlight.getSeatsLeft() > 0 && !overlap) {
						flightSet.add(currentFlight);
						cost += Integer.parseInt(currentFlight.getPrice());
						currentFlight.setSeatsLeft((currentFlight.getSeatsLeft() - 1));
						flightRepository.save(currentFlight);
					}
				}

				if (!flightSet.isEmpty()) {
					Billing billing = new Billing();
					billing.setCost(cost);
					billing = billingRepository.save(billing);

					rs.setPassengerId(Integer.parseInt(passengerId));
					rs.setBillingId(billing.getId());
					rs.setFlights(flightSet);
					Reservation returnResult = reservationRepository.save(rs);

					Passenger pass = passengerRepository.findById(returnResult.getPassengerId()).get(0);
					HashMap<String, List<Flight>> flightsReturned = returnResult.getFlights();
					int price = billingRepository.findById(returnResult.getBillingId()).get(0).getCost();
					int reservationNumber = returnResult.getReservationNumber();

					GetReservationResponse response = new GetReservationResponse();
					response.setFlights(flightsReturned);
					PassengerParameters param = new PassengerParameters(pass.getId(), pass.getFirstname(), pass.getLastname(),
							pass.getAge(), pass.getGender(), pass.getPhone());
					response.setPassenger(param);
					response.setPrice(price);
					response.setReservationNumber(reservationNumber);

					ReservationResponse res2 = new ReservationResponse();
					res2.setReservation(response);

					return res2;
				} else return null;
			} else return null;
		} else return null;
	}

	public List<Reservation> get(int passengerId, String origin, String destination, String flightNumber) {
		List<Reservation> reservations = reservationRepository.serachReservation(passengerId, origin, destination,
				flightNumber);
		if (reservations.size() == 0)return null;
		else return reservations;
	}

	public boolean delete(String number) {
		List<Reservation> reservations = reservationRepository.findByBillingId(Integer.parseInt(number));
		if (reservations.size() > 0){
			List<Flight> flightSet = reservations.get(0).fetchFlights();
			for (Flight flight : flightSet) {
				flight.setSeatsLeft(flight.getSeatsLeft() + 1);
				flightRepository.save(flight);
			}
			billingRepository.delete(Integer.parseInt(number));
			return true;
		}
		else return false;
	}

	public ReservationResponse update(ReservationUpdateParameters params, String billingId) {
		String flightsToAdd[] = {};
		String flightsToRemove[] = {};

		if (params.getFlightsAdded() != null)
			flightsToAdd = params.getFlightsAdded().split(",");

		if (params.getFlightsRemoved() != null)
			flightsToRemove = params.getFlightsRemoved().split(",");
		
		int totalLength = flightsToAdd.length + flightsToRemove.length ;
		int changedFlight = 0 ; 
		
		if ( billingRepository.findById(Integer.parseInt(billingId)).size() > 0 ) {
			List<Reservation> reservationList = reservationRepository.findByBillingId(Integer.parseInt(billingId));
			if (reservationList.size() > 0) {
				
				Reservation rs = reservationList.get(0);
				int passengerId = rs.getPassengerId();
				int cost = 0;

				List<Billing> bl = billingRepository.findById(Integer.parseInt(billingId));
				Billing bill = bl.get(0);
				if (bl.size() > 0)
					cost += bill.getCost();

				// Adding Flight to the reservation
				HashMap<String, String> map = new HashMap<>();

				Reservation reserve = reservationList.get(0);
				List<Flight> setFlights = reserve.fetchFlights();
				for (Flight flight : setFlights) {
					map.put(flight.getNumber(), flight.getNumber());
				}

				List<Flight> flightSet = rs.fetchFlights();

				for (int i = 0; i < flightsToAdd.length; i++) {
					List<Flight> fl = flightRepository.findByNumber(flightsToAdd[i]);
					if (fl.size() > 0) {
						boolean add = true;
						Flight fli = fl.get(0);

						if (map.containsKey(flightsToAdd[i]))
							add = false;

						List<Reservation> reservations = reservationRepository.findByPassengerId(passengerId);
						boolean conflict = false;

						Reservation res = reservations.get(0);
						List<Flight> flights = res.fetchFlights();
						for (Flight flight : flights) {
							Date date2End = flight.getArrivalTime();
							Date date2Start = flight.getDepartureTime();
							Date date1Start = fli.getDepartureTime();
							Date date1End = fli.getArrivalTime();
							if (!helper.compareFlightOverlap(date1Start, date1End, date2Start, date2End))
								conflict = true;
						}

						if (add && !conflict) {
							changedFlight ++ ; 
							flightSet.add(fli);
							
							//Cut the seat by 1
							List<Flight> flightToCutTicketList = flightRepository.findByNumber( fli.getNumber()); 
							Flight flightToCutTicket = flightToCutTicketList.get(0);
							flightToCutTicket.setSeatsLeft(flightToCutTicket.getSeatsLeft()-1);
							flightRepository.save(flightToCutTicket);
							
							cost += Integer.parseInt(fli.getPrice());
							fli.setSeatsLeft(fli.getSeatsLeft() - 1);
							flightRepository.save(fli);
						}
					}
				}

				reservationRepository.save(rs);

				// Deleting flights
				List<Reservation> reservationListNew = reservationRepository.findByBillingId(Integer.parseInt(billingId));
				if (reservationListNew.size() > 0) {
					
					HashMap<String, String> mapNew = new HashMap<>();

					Reservation reserveNew = reservationListNew.get(0);
					List<Flight> setNewFlights = reserveNew.fetchFlights();
					
					for (Flight flight : setNewFlights) {
						mapNew.put(flight.getNumber(), flight.getNumber());
					}

					for (int i = 0; i < flightsToRemove.length; i++) {
						List<Flight> fl = flightRepository.findByNumber(flightsToRemove[i]);
						if (fl.size() > 0) {
							Flight flight = fl.get(0);
							boolean remove = false;
							
							if (mapNew.containsKey(flightsToRemove[i]))
								changedFlight ++ ; 
								setNewFlights.remove(flight);
									
								List<Flight> flightToCutTicketList = flightRepository.findByNumber( flight.getNumber()); 
								Flight flightToCutTicket = flightToCutTicketList.get(0);
								flightToCutTicket.setSeatsLeft(flightToCutTicket.getSeatsLeft()+1);
								flightRepository.save(flightToCutTicket);
								
								cost -= Integer.parseInt(flight.getPrice());
								flight.setSeatsLeft(flight.getSeatsLeft() + 1);
								flightRepository.save(flight);
								
						}
					}
					reserveNew.setFlights(setNewFlights);
					reservationRepository.save(reserveNew);
				}
				
				bill.setCost(cost);
				billingRepository.save(bill);
				
				
				
				if(changedFlight == 0 ) {
					return null ;
				}else {
					// Creating Response
					List<Reservation> res = reservationRepository.findByBillingId(Integer.parseInt(billingId));
					Passenger pass = passengerRepository.findById(res.get(0).getPassengerId()).get(0);
					HashMap<String, List<Flight>> flights = res.get(0).getFlights();
					int price = billingRepository.findById(res.get(0).getBillingId()).get(0).getCost();
					int reservationNumber = res.get(0).getReservationNumber();

					GetReservationResponse response = new GetReservationResponse();
					response.setFlights(flights);
					PassengerParameters param = new PassengerParameters(pass.getId(), pass.getFirstname(), pass.getLastname(),pass.getAge(), pass.getGender(), pass.getPhone());
					response.setPassenger(param);
					response.setPrice(price);
					response.setReservationNumber(reservationNumber);

					ReservationResponse res2 = new ReservationResponse();
					res2.setReservation(response);
					return res2;
				}
				
			} else return null;
		}else return null ; 
	}

	public ReservationResponse getByNumber(String number) {
		List<Reservation> res = reservationRepository.findByBillingId(Integer.parseInt(number));
		if (res.size() > 0) {
			Passenger pass = passengerRepository.findById(res.get(0).getPassengerId()).get(0);
			HashMap<String, List<Flight>> flights = res.get(0).getFlights();
			int price = billingRepository.findById(res.get(0).getBillingId()).get(0).getCost();
			int reservationNumber = res.get(0).getReservationNumber();

			GetReservationResponse response = new GetReservationResponse();
			response.setFlights(flights);
			PassengerParameters param = new PassengerParameters(pass.getId(), pass.getFirstname(), pass.getLastname(),pass.getAge(), pass.getGender(), pass.getPhone());
			response.setPassenger(param);
			response.setPrice(price);
			response.setReservationNumber(reservationNumber);

			ReservationResponse res2 = new ReservationResponse();
			res2.setReservation(response);
			
			return res2;
		} else return null;
	}

}
