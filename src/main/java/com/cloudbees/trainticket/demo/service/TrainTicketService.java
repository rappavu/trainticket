package com.cloudbees.trainticket.demo.service;

import java.util.Map;

import com.cloudbees.trainticket.demo.model.Seat;
import com.cloudbees.trainticket.demo.model.Ticket;
import com.cloudbees.trainticket.demo.model.User;

public interface TrainTicketService {
	public Ticket buyTicket(User user);
	public Ticket getTicketDetails(int ticketNumber);
	public void updateSeat(int ticketNumber, Seat seat);
	public void cancelTicket(int ticketNumber);
	public Map<Integer, User> getSectionUsers(String section);
}
