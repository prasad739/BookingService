package com.prasad.booking.service;

import com.prasad.booking.dto.BookingDto;
import com.prasad.booking.dto.ResponseDto;
import com.prasad.booking.entity.Booking;

public interface IBookingService {

	// method to get the bookingId to book the products
	public Booking addBooking(BookingDto booking);

	// method to get the booking details
	public ResponseDto getBookingById(int bookingId);

	// method to delete the booking
	public String deleteBookingById(int bookingId);

	// method to update the booking
	public Booking updateBooking(int bookingId, BookingDto updatedBooking);

	// method to calculate the entire bill of the product
	public double calculateBill(int bookingId);

}
