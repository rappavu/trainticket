package com.cloudbees.trainticket.demo.serice.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cloudbees.trainticket.demo.exception.BadSeatNumberException;
import com.cloudbees.trainticket.demo.exception.BadSectionException;
import com.cloudbees.trainticket.demo.exception.CancelledTicketException;
import com.cloudbees.trainticket.demo.exception.InvalidTicketNumberException;
import com.cloudbees.trainticket.demo.exception.NoSeatAvailableException;
import com.cloudbees.trainticket.demo.exception.SeatInUseException;
import com.cloudbees.trainticket.demo.model.Seat;
import com.cloudbees.trainticket.demo.model.Ticket;
import com.cloudbees.trainticket.demo.model.User;
import com.cloudbees.trainticket.demo.service.impl.TrainTicketServiceImpl;

public class TrainTicketServiceImplTest {
	
	private TrainTicketServiceImpl trainTicketServiceImpl;
	
	private User u1, u2;
	private Seat s1, s2;
	private Map<Integer, User> sectionUsers;
	
	@BeforeEach
	public void setUp() {
		trainTicketServiceImpl = new TrainTicketServiceImpl();
		s1 = new Seat("A", 1);
		s2 = new Seat("A", 2);
		u1 = new User("Sachin", "Tendulkar", "sachin@gmail.com");
		u2 = new User("Virat", "Kohli", "virat@gmail.com");
		sectionUsers = new HashMap<Integer, User>();
		sectionUsers.put(1, u1);
		sectionUsers.put(2, u2);
	}
	
	@AfterEach
	public void tearDown() {
		
	}
	
	@Test
	public void testBuyTicket1() {
		Ticket actual = trainTicketServiceImpl.buyTicket(u1);
		assertEquals(u1, actual.getUser());
		assertEquals(s1, actual.getSeat());
	}
	
	@Test
	public void testBuyTicket2() {
		// test if we get no seat available exception
		// we have 20 seats 
		for (int i = 0; i < 20; i++) {
			trainTicketServiceImpl.buyTicket(u1);
		}
		try {
			trainTicketServiceImpl.buyTicket(u1);
		}
		catch (NoSeatAvailableException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testGetTicketDetails1() {
		Ticket t1 = trainTicketServiceImpl.buyTicket(u1);
		Ticket actual = trainTicketServiceImpl.getTicketDetails(t1.getTicketNumber());
		assertEquals(t1, actual);
	}
	
	@Test
	public void testGetTicketDetails2() {
		trainTicketServiceImpl.buyTicket(u1);
		try {
			trainTicketServiceImpl.getTicketDetails(1234);
		}
		catch (InvalidTicketNumberException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testCancelTicket1() {
		Ticket t1 = trainTicketServiceImpl.buyTicket(u1);
		assertEquals(false, t1.isCancelled());
		
		trainTicketServiceImpl.cancelTicket(t1.getTicketNumber());
		Ticket t2 = trainTicketServiceImpl.getTicketDetails(t1.getTicketNumber());
		assertEquals(t1.getTicketNumber(), t2.getTicketNumber());
		assertEquals(true, t2.isCancelled());
	}
	
	@Test
	public void testCancelTicket2() {
		Ticket t1 = trainTicketServiceImpl.buyTicket(u1);
		assertEquals(false, t1.isCancelled());
		
		try {
			trainTicketServiceImpl.cancelTicket(999);
		}
		catch (InvalidTicketNumberException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testUpdateSeat1() {
		Ticket t1 = trainTicketServiceImpl.buyTicket(u1);
		assertEquals(s1, t1.getSeat());
		
		trainTicketServiceImpl.updateSeat(t1.getTicketNumber(), s2);
		Ticket t2 = trainTicketServiceImpl.getTicketDetails(t1.getTicketNumber());
		assertEquals(t1.getTicketNumber(), t2.getTicketNumber());
		assertEquals(s2, t2.getSeat());
	}
	
	@Test
	public void testUpdateSeat2() {
		Ticket t1 = trainTicketServiceImpl.buyTicket(u1);
		Ticket t2 = trainTicketServiceImpl.buyTicket(u1);
		
		try {
			trainTicketServiceImpl.updateSeat(t1.getTicketNumber(), t2.getSeat());
		}
		catch (SeatInUseException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testUpdateSeat3() {
		Ticket t1 = trainTicketServiceImpl.buyTicket(u1);
		Seat s = new Seat("C", 10);
		try {
			trainTicketServiceImpl.updateSeat(t1.getTicketNumber(), s);
		}
		catch (BadSectionException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testUpdateSeat4() {
		Ticket t1 = trainTicketServiceImpl.buyTicket(u1);
		Seat s = new Seat("A", 11);
		try {
			trainTicketServiceImpl.updateSeat(t1.getTicketNumber(), s);
		}
		catch (BadSeatNumberException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testUpdateSeat5() {
		trainTicketServiceImpl.buyTicket(u1);
		try {
			trainTicketServiceImpl.updateSeat(1234, s2);
		}
		catch (InvalidTicketNumberException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testUpdateSeat6() {
		Ticket t1 = trainTicketServiceImpl.buyTicket(u1);
		trainTicketServiceImpl.cancelTicket(t1.getTicketNumber());
		try {
			trainTicketServiceImpl.updateSeat(t1.getTicketNumber(), s2);
		}
		catch (CancelledTicketException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testGetSectionUsers1() {
		trainTicketServiceImpl.buyTicket(u1);
		trainTicketServiceImpl.buyTicket(u2);
		Map<Integer, User> actualUsers = trainTicketServiceImpl.getSectionUsers("A");
		assertEquals(sectionUsers, actualUsers);
	}
	
	@Test
	public void testGetSectionUsers2() {
		trainTicketServiceImpl.buyTicket(u1);
		trainTicketServiceImpl.buyTicket(u2);
		try {
			trainTicketServiceImpl.getSectionUsers("C");
		}
		catch (BadSectionException e) {
			return;
		}
		fail();
	}
}
