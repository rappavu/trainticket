package com.cloudbees.trainticket.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudbees.trainticket.demo.model.Seat;
import com.cloudbees.trainticket.demo.model.Ticket;
import com.cloudbees.trainticket.demo.model.User;
import com.cloudbees.trainticket.demo.service.TrainTicketService;
import com.cloudbees.trainticket.demo.util.Message;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/trainticket")
public class TrainTicketController {
	private static final Logger log = LoggerFactory.getLogger(TrainTicketController.class);
	private static final ResourceBundle rb = ResourceBundle.getBundle("messages");
	
	private TrainTicketService trainTicketService;
	
	public TrainTicketController(TrainTicketService service) {
		this.trainTicketService = service;
	}
	
	@Operation(summary = "Book a ticket for the specified user")
	@PostMapping("")
	public ResponseEntity<Object> buyTicket(@RequestBody User user) {
		log.debug("buyTicket() called with {}", user);
		
		Ticket ticket = trainTicketService.buyTicket(user);
		log.info("{} issued with {}", user, ticket);
		
		Map<String, Object> response = new HashMap<String, Object>();
        response.put("ticket", ticket);
		return new ResponseEntity<>(response, HttpStatus.OK); 
	}
	
	@Operation(summary = "Get the ticket details for the given ticket number")
	@GetMapping("/{ticketNumber}")
	public ResponseEntity<Object> getTicketDetails(@PathVariable("ticketNumber") int ticketNumber) {
		log.debug("getTicketDetails() called with ticketNumber {}", ticketNumber);
		
		Ticket ticket = trainTicketService.getTicketDetails(ticketNumber);
		
		Map<String, Object> response = new HashMap<>();
        response.put("ticket", ticket);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@Operation(summary = "Update the seat details for the given ticket")
	@PutMapping("/{ticketNumber}")
	public ResponseEntity<Object> updateSeat(@PathVariable("ticketNumber") int ticketNumber, @RequestBody Seat seat) {
		log.debug("updateSeat() called with ticketNumber {} and seat {}", ticketNumber, seat);
		
		trainTicketService.updateSeat(ticketNumber, seat);
		log.info("Seat updated for ticket {} with {}", ticketNumber, seat);

		return new ResponseEntity<>(rb.getString(Message.SEAT_UPDATED), HttpStatus.OK);
	}
	
	@Operation(summary = "Cancel the specified ticket and free the seat assigned")
	@DeleteMapping("/{ticketNumber}")
	public ResponseEntity<Object> cancelTicket(@PathVariable("ticketNumber") int ticketNumber) {
		log.debug("cancelTicket() called with ticketNumber {}", ticketNumber);
		
		trainTicketService.cancelTicket(ticketNumber);
		log.info("ticket {} is cancelled", ticketNumber);
		
		return new ResponseEntity<>(rb.getString(Message.CANCEL_SUCCESS), HttpStatus.OK);
	}
	
	@Operation(summary = "Get the details of the users and their seat number in the given section of the train")
	@GetMapping("/section/{section}")
	public ResponseEntity<Object> getSectionUsers(@PathVariable("section") String section) {
		log.debug("getSectionUsers() called with section {}", section);
		
		Map<Integer, User> users = trainTicketService.getSectionUsers(section);
		
		Map<String, Object> response = new HashMap<>();
        response.put("section", section);
		ArrayList<Object> list = new ArrayList<>();
		for (Map.Entry<Integer, User> entry : users.entrySet()) {
			Map<String, Object> elem = new HashMap<>(); 
			elem.put("seatNumber",  entry.getKey());
    		elem.put("user", entry.getValue());
    		list.add(elem);
		}
        response.put("users",  list);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
