package com.prasad.booking.exception;

@SuppressWarnings("serial")
public class NoProductsBookedException extends RuntimeException {

	public NoProductsBookedException(String msg) {
		super(msg);
	}

}
