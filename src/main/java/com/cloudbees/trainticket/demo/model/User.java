package com.cloudbees.trainticket.demo.model;

public class User {
	private String firstName;
	private String lastName;
	private String email;
	
	public User(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	@Override
	public boolean equals(Object o) {
		User other = (User)o;
		return this.firstName.equals(other.firstName) && 
				this.lastName.equals(other.lastName) && 
				this.email.equals(other.email);
	}
	
	@Override
	public int hashCode() {
		return (firstName+lastName+email).hashCode();
	}
	
	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + "]";
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
