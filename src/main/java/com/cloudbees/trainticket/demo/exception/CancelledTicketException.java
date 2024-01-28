package com.cloudbees.trainticket.demo.exception;

import com.cloudbees.trainticket.demo.model.Ticket;

public class CancelledTicketException extends RuntimeException {
	private static final long serialVersionUID = 6100635257768424778L;
	
	private Ticket ticket;
	
	public CancelledTicketException(Ticket t) {
		super("Cancelled Ticket");
		this.ticket = t;
	}

	public Ticket getTicket() {
		return ticket;
	}
}
