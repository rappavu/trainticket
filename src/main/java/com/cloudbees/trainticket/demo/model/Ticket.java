package com.cloudbees.trainticket.demo.model;

public class Ticket {
	private static int nextTicketNumber = 1;
	private int ticketNumber;
	private String from;
	private String to;
	private double amount;
	private Seat seat;
	private User user;
	private boolean isCancelled;
	
	public Ticket(String from, String to, double amount, Seat seat, User user) {
		this.ticketNumber = nextTicketNumber;
		nextTicketNumber++;
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.seat = seat;
		this.user = user;
		this.isCancelled = false;
	}

	@Override
	public boolean equals(Object o) {
		Ticket other = (Ticket)o;
		return this.ticketNumber == other.ticketNumber &&
				this.from.equals(other.from) && 
				this.to.equals(other.to) && 
				this.amount == other.amount &&
				this.seat.equals(other.seat) &&
				this.user.equals(other.user) &&
				this.isCancelled == other.isCancelled;
	}
	
	@Override 
	public int hashCode() {
		return ticketNumber;
	}
	
	@Override
	public String toString() {
		return "Ticket [ticketNumber=" + ticketNumber + ", from=" + from + ", to=" + to + ", amount=" + amount
				+ ", seat=" + seat + ", user=" + user + ", isCancelled=" + isCancelled + "]";
	}

	public int getTicketNumber() {
		return ticketNumber;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public double getAmount() {
		return amount;
	}

	public Seat getSeat() {
		return seat;
	}

	public void setSeat(Seat seat) {
		this.seat = seat;
	}

	public User getUser() {
		return user;
	}
	
	public boolean isCancelled() {
		return isCancelled;
	}
	
	public void cancel() {
		this.isCancelled = true;
	}
}
