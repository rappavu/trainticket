package com.cloudbees.trainticket.demo.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cloudbees.trainticket.demo.exception.BadSeatNumberException;
import com.cloudbees.trainticket.demo.exception.BadSectionException;
import com.cloudbees.trainticket.demo.exception.CancelledTicketException;
import com.cloudbees.trainticket.demo.exception.InvalidTicketNumberException;
import com.cloudbees.trainticket.demo.exception.NoSeatAvailableException;
import com.cloudbees.trainticket.demo.exception.SeatInUseException;
import com.cloudbees.trainticket.demo.model.Seat;
import com.cloudbees.trainticket.demo.model.Ticket;
import com.cloudbees.trainticket.demo.model.User;
import com.cloudbees.trainticket.demo.service.TrainTicketService;
import com.cloudbees.trainticket.demo.util.Message;

import com.google.gson.Gson;

import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;


@WebMvcTest(TrainTicketController.class)
public class TrainTicketControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private TrainTicketService trainTicketService;
	
	private ResourceBundle rb;
	private Gson gson;
	private Ticket t1;
	private User u1, u2;
	private Seat s1, s2;
	private Map<Integer, User> sectionUsers;
	
	@BeforeEach
	public void setUp() {
		rb = ResourceBundle.getBundle("messages");
		gson = new Gson();
		s1 = new Seat("A", 1);
		s2 = new Seat("A", 2);
		u1 = new User("Sachin", "Tendulkar", "sachin@gmail.com");
		u2 = new User("Virat", "Kohli", "virat@gmail.com");
		t1 = new Ticket("London", "Paris", 20.0, s1, u1);
		sectionUsers = new HashMap<Integer, User>();
		sectionUsers.put(1, u1);
		sectionUsers.put(2, u2);
	}
	
	@AfterEach
	public void tearDown() {
		
	}
	
	@Test
	public void testBuyTicket1() throws Exception {
		when(trainTicketService.buyTicket(eq(u1))).thenReturn(t1);
		
		MvcResult result = mockMvc.perform(post("/trainticket")
							.contentType(MediaType.APPLICATION_JSON)
							.content(gson.toJson(u1)))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String s = result.getResponse().getContentAsString();
		Ticket actual = gson.fromJson(s, BuyTicketResponse.class).ticket;
		assertEquals(t1, actual);
	}
	
	@Test
	public void testBuyTicket2() throws Exception {
		when(trainTicketService.buyTicket(any(User.class)))
			.thenThrow(new NoSeatAvailableException());
		
		MvcResult result = mockMvc.perform(post("/trainticket")
							.contentType(MediaType.APPLICATION_JSON)
							.content(gson.toJson(u1)))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();
		
		String s = result.getResponse().getContentAsString();
		assertEquals(rb.getString(Message.NO_SEAT_AVAILABLE), s);
	}
	
	@Test
	public void testGetTicketDetails1() throws Exception {
		when(trainTicketService.getTicketDetails(eq(t1.getTicketNumber()))).thenReturn(t1);
		
		MvcResult result = mockMvc.perform(get("/trainticket/"+t1.getTicketNumber()))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String s = result.getResponse().getContentAsString();
		Ticket actual = gson.fromJson(s, BuyTicketResponse.class).ticket;
		assertEquals(t1, actual);
	}
	
	@Test
	public void testGetTicketDetails2() throws Exception {
		when(trainTicketService.getTicketDetails(eq(t1.getTicketNumber())))
			.thenThrow(new InvalidTicketNumberException(t1.getTicketNumber()));
		
		MvcResult result = mockMvc.perform(get("/trainticket/"+t1.getTicketNumber()))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String actual = result.getResponse().getContentAsString();
		String expected = String.format(rb.getString(Message.INVALID_TICKET_NUMBER), t1.getTicketNumber());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateSeat1() throws Exception {
		MvcResult result = mockMvc.perform(put("/trainticket/"+t1.getTicketNumber())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(s2)))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String actual = result.getResponse().getContentAsString();
		String expected = rb.getString(Message.SEAT_UPDATED);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateSeat2() throws Exception {
		doThrow(new SeatInUseException(s2)).when(trainTicketService)
			.updateSeat(t1.getTicketNumber(), s2);
		
		MvcResult result = mockMvc.perform(put("/trainticket/"+t1.getTicketNumber())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(s2)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String actual = result.getResponse().getContentAsString();
		String expected = String.format(rb.getString(Message.SEAT_IN_USE), s2.getNumber(), s2.getSection());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateSeat3() throws Exception {
		doThrow(new InvalidTicketNumberException(t1.getTicketNumber())).when(trainTicketService)
			.updateSeat(t1.getTicketNumber(), s2);
		
		MvcResult result = mockMvc.perform(put("/trainticket/"+t1.getTicketNumber())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(s2)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String actual = result.getResponse().getContentAsString();
		String expected = String.format(rb.getString(Message.INVALID_TICKET_NUMBER), t1.getTicketNumber());
		assertEquals(expected, actual);
	} 
	
	@Test
	public void testUpdateSeat4() throws Exception {
		doThrow(new CancelledTicketException(t1)).when(trainTicketService)
			.updateSeat(t1.getTicketNumber(), s2);
		
		MvcResult result = mockMvc.perform(put("/trainticket/"+t1.getTicketNumber())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(s2)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String actual = result.getResponse().getContentAsString();
		String expected = String.format(rb.getString(Message.TICKET_CANCELLED), t1.getTicketNumber());
		assertEquals(expected, actual);
	} 
	
	@Test
	public void testUpdateSeat5() throws Exception {
		String section = "C";
		doThrow(new BadSectionException(section)).when(trainTicketService)
			.updateSeat(t1.getTicketNumber(), s2);
		
		MvcResult result = mockMvc.perform(put("/trainticket/"+t1.getTicketNumber())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(s2)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String actual = result.getResponse().getContentAsString();
		String expected = String.format(rb.getString(Message.BAD_SECTION), section);
		assertEquals(expected, actual);
	} 
	
	@Test
	public void testUpdateSeat6() throws Exception {
		int seatNumber = 99;
		doThrow(new BadSeatNumberException(seatNumber)).when(trainTicketService)
			.updateSeat(t1.getTicketNumber(), s2);
		
		MvcResult result = mockMvc.perform(put("/trainticket/"+t1.getTicketNumber())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(s2)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String actual = result.getResponse().getContentAsString();
		String expected = String.format(rb.getString(Message.BAD_SEAT), seatNumber);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCancelTicket1() throws Exception {	
		MvcResult result = mockMvc.perform(delete("/trainticket/"+t1.getTicketNumber()))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String actual = result.getResponse().getContentAsString();
		String expected = rb.getString(Message.CANCEL_SUCCESS);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCancelTicket2() throws Exception {
		doThrow(new InvalidTicketNumberException(t1.getTicketNumber())).when(trainTicketService)
			.cancelTicket(eq(t1.getTicketNumber()));
		
		MvcResult result = mockMvc.perform(delete("/trainticket/"+t1.getTicketNumber()))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String actual = result.getResponse().getContentAsString();
		String expected = String.format(rb.getString(Message.INVALID_TICKET_NUMBER), t1.getTicketNumber());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetSectionUsers1() throws Exception {
		when(trainTicketService.getSectionUsers(eq("A")))
			.thenReturn(sectionUsers);
		
		MvcResult result = mockMvc.perform(get("/trainticket/section/A"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String s = result.getResponse().getContentAsString();
		GetSectionUsersResponse r = gson.fromJson(s, GetSectionUsersResponse.class);
		
		String section = r.section;
		assertEquals("A", section);
		
		Map<Integer, User> actualSectionUsers = toSectionUsers(r.users);
		assertEquals(sectionUsers, actualSectionUsers);
	}
	
	@Test
	public void testGetSectionUsers2() throws Exception {
		String section = "C";
		when(trainTicketService.getSectionUsers(eq(section)))
			.thenThrow(new BadSectionException(section));
		
		MvcResult result = mockMvc.perform(get("/trainticket/section/"+section))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String actual = result.getResponse().getContentAsString();
		String expected = String.format(rb.getString(Message.BAD_SECTION), section);
		assertEquals(expected, actual);
	}
	
	public Map<Integer, User> toSectionUsers(List<SeatUser> list) {
		Map<Integer, User> sectionUsers = new HashMap<>();
		for (SeatUser su : list) {
			sectionUsers.put(su.seatNumber, su.user);
		}
		return sectionUsers;
	}
}

class BuyTicketResponse {
	Ticket ticket;
}

class GetSectionUsersResponse {
	String section;
	List<SeatUser> users;
}

class SeatUser {
	int seatNumber;
	User user;
}

