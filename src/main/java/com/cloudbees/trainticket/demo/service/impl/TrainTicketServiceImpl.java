package com.cloudbees.trainticket.demo.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cloudbees.trainticket.demo.exception.BadSeatNumberException;
import com.cloudbees.trainticket.demo.exception.BadSectionException;
import com.cloudbees.trainticket.demo.exception.CancelledTicketException;
import com.cloudbees.trainticket.demo.exception.InvalidTicketNumberException;
import com.cloudbees.trainticket.demo.exception.SeatInUseException;
import com.cloudbees.trainticket.demo.exception.NoSeatAvailableException;
import com.cloudbees.trainticket.demo.model.Seat;
import com.cloudbees.trainticket.demo.model.Ticket;
import com.cloudbees.trainticket.demo.model.User;
import com.cloudbees.trainticket.demo.service.TrainTicketService;

@Service
public class TrainTicketServiceImpl implements TrainTicketService {
	private static final Logger log = LoggerFactory.getLogger(TrainTicketServiceImpl.class);

	// section and the seats in the section
	// if occupied has the corresponding ticket issued for the seat
	// we assume two sections A and B with 10 seats in each section
	private Map<String, Ticket[]> seats;
	
	// list of available seats in each section
	private Map<String, Set<Integer>> freeSeats;
	
	// issued tickets
	private Map<Integer, Ticket> tickets;
	
	public TrainTicketServiceImpl() {
		// initialize seats
		seats = new HashMap<String, Ticket[]>();
		seats.put("A", new Ticket[10]);
		seats.put("B", new Ticket[10]);
		
		// initialize free seats
		freeSeats = new HashMap<String, Set<Integer>>();
		freeSeats.put("A", new HashSet<Integer>());
		freeSeats.put("B", new HashSet<Integer>());
		// seat numbers are zero-indexed internally one-indexed externally for users
		for (int i = 0; i < 10; i++) {
			freeSeats.get("A").add(i);
			freeSeats.get("B").add(i);
		}
		
		// initialize issued tickets map
		tickets = new HashMap<Integer, Ticket>();
		
		log.debug("TrainTicketServiceImpl() instantiated");
	}
	
	public synchronized Ticket buyTicket(User user) { 
		log.debug("buyTicket() called with {}", user);
		
		// check for a free seat
		String section;
		Iterator<Integer> iterator;
		if (freeSeats.get("A").size() > 0) {
			section = "A";
			iterator = freeSeats.get("A").iterator();
		} else if (freeSeats.get("B").size() > 0) {
			section = "B";
			iterator = freeSeats.get("B").iterator();
		} else {
			throw new NoSeatAvailableException();
		}
		
		// get the seat number and remove it from the free list
		int seatNumber = iterator.next() + 1;
		iterator.remove(); 	
		Seat seat = new Seat(section, seatNumber);
		
		// generate ticket
		Ticket ticket = new Ticket("London", "Paris", 20.0, seat, user);
		tickets.put(ticket.getTicketNumber(), ticket);
		
		// book this seat
		seats.get(section)[seatNumber-1] = ticket;
		
		log.debug("buyTicket() returned with {}", ticket);
		return ticket;
	}

	public synchronized Ticket getTicketDetails(int ticketNumber) {
		log.debug("getTicketDetails() called with ticketNumber {}", ticketNumber);
		
		if (!tickets.containsKey(ticketNumber)) {
			throw new InvalidTicketNumberException(ticketNumber);
		}
		
		return tickets.get(ticketNumber);
	}

	public synchronized void updateSeat(int ticketNumber, Seat seat) {
		log.debug("updateSeat() called with ticket {} and {}", ticketNumber, seat);
		
		// make sure the ticket is valid
		if (!tickets.containsKey(ticketNumber)) {
			throw new InvalidTicketNumberException(ticketNumber);
		}
		
		Ticket ticket = tickets.get(ticketNumber);
		if (ticket.isCancelled()) {
			throw new CancelledTicketException(ticket);
		}
		
		// check if this seat is valid
		if (!(seat.getSection().equals("A") || seat.getSection().equals("B"))) {
			throw new BadSectionException(seat.getSection());
		}
		if (!(seat.getNumber() > 0 && seat.getNumber() < 11)) {
			throw new BadSeatNumberException(seat.getNumber());
		}
		
		// check if this seat is available 
		if (seats.get(seat.getSection())[seat.getNumber()-1] != null) {
			throw new SeatInUseException(seat);
		}

		// free the old seat
		Seat oldSeat = ticket.getSeat();
		seats.get(oldSeat.getSection())[oldSeat.getNumber()-1] = null;
		freeSeats.get(oldSeat.getSection()).add(oldSeat.getNumber()-1);
		
		// assign the new seat and remove it from the free list
		ticket.setSeat(seat);
		seats.get(seat.getSection())[seat.getNumber()-1] = ticket;
		freeSeats.get(seat.getSection()).remove(seat.getNumber()-1);
		
		log.debug("updateSeat() completed successfully - {}", ticket);
	}

	public synchronized void cancelTicket(int ticketNumber) {
		log.debug("cancelTicket() called for ticket {}", ticketNumber);
		
		if (!tickets.containsKey(ticketNumber)) {
			throw new InvalidTicketNumberException(ticketNumber);
		}
		
		Ticket ticket = tickets.get(ticketNumber);
		if (ticket.isCancelled()) {
			// already cancelled
			log.debug("cancelTicket() ticket {} already cancelled", ticketNumber);
			return;
		}
		 
		// free the seat
		Seat seat = ticket.getSeat();
		seats.get(seat.getSection())[seat.getNumber()-1] = null;
		freeSeats.get(seat.getSection()).add(seat.getNumber()-1);
		log.info("ticket {} cancelled and {} is freed", ticketNumber, seat);
		
		ticket.cancel();
	}

	public synchronized Map<Integer, User> getSectionUsers(String section) throws BadSectionException {
		log.debug("getSectionUsers() called for section {}", section);
		
		if (!section.equals("A") && !section.equals("B")) {
			throw new BadSectionException(section);
		}
		
		Map<Integer, User> seatUsers = new HashMap<Integer, User>();
		for (Ticket ticket : seats.get(section)) {
			if (ticket == null) continue;
			seatUsers.put(ticket.getSeat().getNumber(), ticket.getUser());
		}
		
		return seatUsers;
	}

}
