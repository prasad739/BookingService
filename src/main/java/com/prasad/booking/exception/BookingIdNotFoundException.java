package com.prasad.booking.exception;

@SuppressWarnings("serial")
public class BookingIdNotFoundException extends RuntimeException {

	public BookingIdNotFoundException(String msg) {
		super(msg);
	}

}
