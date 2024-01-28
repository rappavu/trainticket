package com.cloudbees.trainticket.demo.exception;

public class BadSeatNumberException extends RuntimeException {
	private static final long serialVersionUID = -6244876537900968939L;
	
	private int seatNumber;
	
	public BadSeatNumberException(int number) {
		super("Bad seat number");
		this.seatNumber = number;
	}

	public int getSeatNumber() {
		return seatNumber;
	}
}
