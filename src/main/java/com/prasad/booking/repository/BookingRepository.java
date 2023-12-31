package com.prasad.booking.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.prasad.booking.entity.Booking;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer> {

	List<Booking> findAllByCustomerId(int customerId);


}
