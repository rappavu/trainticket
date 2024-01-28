package com.cloudbees.trainticket.demo.exception;

import com.cloudbees.trainticket.demo.model.Seat;

public class SeatInUseException extends RuntimeException {
	private static final long serialVersionUID = 4776513351230983023L;

	private Seat seat;
	
	public SeatInUseException(Seat s) {
		super("Seat is in use");
		this.seat = s;
	}
	
	public Seat getSeat() {
		return seat;
	}
}
