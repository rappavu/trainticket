package com.cloudbees.trainticket.demo.exception;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.cloudbees.trainticket.demo.util.Message;

@ControllerAdvice
public class TrainTicketExceptionHandler { 
	private static final Logger log = LoggerFactory.getLogger(TrainTicketExceptionHandler.class);	
	private static final ResourceBundle rb = ResourceBundle.getBundle("messages");
	
	@ExceptionHandler(BadSectionException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleBadSectionException(BadSectionException e) {
		log.error("Got Exception - ", e);	
		String mesg = String.format(rb.getString(Message.BAD_SECTION), e.getSection());
		return new ResponseEntity<>(mesg, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(BadSeatNumberException.class)
	public ResponseEntity<Object> handleBadSeatNumberException(BadSeatNumberException e) {
		log.error("Got Exception - ", e);	
		String mesg = String.format(rb.getString(Message.BAD_SEAT), e.getSeatNumber());
		return new ResponseEntity<>(mesg, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(CancelledTicketException.class)
	public ResponseEntity<Object> handleCancelledTicketException(CancelledTicketException e) {
		log.error("Got Exception - ", e);	
		String mesg = String.format(rb.getString(Message.TICKET_CANCELLED), e.getTicket().getTicketNumber());
		return new ResponseEntity<>(mesg, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidTicketNumberException.class)
	public ResponseEntity<Object> handleInvalidTicketNumberException(InvalidTicketNumberException e) {
		log.error("Got Exception - ", e);	
		String mesg = String.format(rb.getString(Message.INVALID_TICKET_NUMBER), e.getTicketNumber());
		return new ResponseEntity<>(mesg, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoSeatAvailableException.class)
	public ResponseEntity<Object> handleNoSeatAvailableException(NoSeatAvailableException e) {
		log.error("Got Exception - ", e);	
		String mesg = rb.getString(Message.NO_SEAT_AVAILABLE);
		return new ResponseEntity<>(mesg, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(SeatInUseException.class)
	public ResponseEntity<Object> handleSeatInUseException(SeatInUseException e) {
		log.error("Got Exception - ", e);	
		String mesg = String.format(rb.getString(Message.SEAT_IN_USE), 
				e.getSeat().getNumber(), e.getSeat().getSection());
		return new ResponseEntity<>(mesg, HttpStatus.BAD_REQUEST);
	}
}
