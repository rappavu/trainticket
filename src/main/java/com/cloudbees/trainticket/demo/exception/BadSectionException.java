package com.cloudbees.trainticket.demo.exception;

public class BadSectionException extends RuntimeException {
	private static final long serialVersionUID = 4615029099895284244L;

	private String section;
	
	public BadSectionException(String section) {
		super("Bad Section");
		this.section = section;
	}
	
	public String getSection() {
		return section;
	}
}
