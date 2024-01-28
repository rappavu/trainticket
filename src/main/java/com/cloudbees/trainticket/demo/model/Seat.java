package com.cloudbees.trainticket.demo.model;

public class Seat {
	private String section;		// either A or B
	private int number;

	public Seat(String section, int number) {
		this.section = section;
		this.number = number;
	}
	
	@Override
	public boolean equals(Object o) {
		Seat other = (Seat)o;
		return this.section.equals(other.section) && 
				this.number == other.number;
	}
	
	@Override
	public int hashCode() {
		return (section+number).hashCode();
	}

	@Override
	public String toString() {
		return "Seat [section=" + section + ", number=" + number + "]";
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
