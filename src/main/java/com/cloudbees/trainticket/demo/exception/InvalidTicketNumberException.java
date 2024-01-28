package com.cloudbees.trainticket.demo.exception;

public class InvalidTicketNumberException extends RuntimeException {
	private static final long serialVersionUID = 5452900703705858320L;
	
	private int ticketNumber;

	public InvalidTicketNumberException(int ticketNumber) {
		super("Invalid Ticket Number");
		this.ticketNumber = ticketNumber;
	}

	public int getTicketNumber() {
		return ticketNumber;
	}
}
