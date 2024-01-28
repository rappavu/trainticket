package com.cloudbees.trainticket.demo.exception;

public class NoSeatAvailableException extends RuntimeException {
	private static final long serialVersionUID = -933857775709724819L;

	public NoSeatAvailableException() {
		super("No seat is available");
	}
	
}
