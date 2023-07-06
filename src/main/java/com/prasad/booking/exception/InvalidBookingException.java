package com.prasad.booking.exception;

@SuppressWarnings("serial")
public class InvalidBookingException extends RuntimeException {
	
	public InvalidBookingException() {
		
	}
	
	public InvalidBookingException(String msg) {
		super(msg);
	}

}
