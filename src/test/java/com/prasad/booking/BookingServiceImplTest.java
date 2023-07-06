package com.prasad.booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.prasad.booking.dto.BookingDto;
import com.prasad.booking.dto.CustomerDto;
import com.prasad.booking.dto.ProductDto;
import com.prasad.booking.dto.ResponseDto;
import com.prasad.booking.entity.Booking;
import com.prasad.booking.exception.BookingIdNotFoundException;
import com.prasad.booking.exception.CustomerNotFoundException;
import com.prasad.booking.exception.InvalidProductIdException;
import com.prasad.booking.exception.NoProductsBookedException;
import com.prasad.booking.repository.BookingRepository;
import com.prasad.booking.service.BookingServiceImpl;

class BookingServiceImplTest {

	@Mock
	private BookingRepository bookingRepo;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private BookingServiceImpl bookingService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testAddBooking_ValidBookingDto_Success() {
		// Arrange
		BookingDto bookingDto = new BookingDto();
		bookingDto.setProductId(1);
		bookingDto.setCustomerId(1);
		bookingDto.setDateTime(null);

		ProductDto productDto = new ProductDto();
		productDto.setProductId(1);
		productDto.setCharges(100);
		productDto.setCategory("Category");
		productDto.setSize("Size");
		productDto.setName("Product Name");

		ResponseEntity<ProductDto> productResponse = new ResponseEntity<>(productDto, HttpStatus.OK);
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(ProductDto.class)))
				.thenReturn(productResponse);

		CustomerDto customerDto = new CustomerDto();
		customerDto.setCustomerId(1);

		ResponseEntity<CustomerDto> customerResponse = new ResponseEntity<>(customerDto, HttpStatus.OK);
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(CustomerDto.class)))
				.thenReturn(customerResponse);

		Booking savedBooking = new Booking();
		savedBooking.setBookingId(1);
		savedBooking.setProductId(1);
		savedBooking.setCharges(100);
		savedBooking.setCategory("Category");
		savedBooking.setSize("Size");
		savedBooking.setName("Product Name");
		savedBooking.setCustomerId(1);
		savedBooking.setDateTime(null);

		Mockito.when(bookingRepo.save(Mockito.any(Booking.class))).thenReturn(savedBooking);

		// Act
		Booking result = bookingService.addBooking(bookingDto);

		// Assert

		Assertions.assertNotNull(result);
		Assertions.assertEquals(savedBooking.getBookingId(), result.getBookingId());
		Assertions.assertEquals(savedBooking.getProductId(), result.getProductId());
		Assertions.assertEquals(savedBooking.getCharges(), result.getCharges());
		Assertions.assertEquals(savedBooking.getCategory(), result.getCategory());
		Assertions.assertEquals(savedBooking.getSize(), result.getSize());
		Assertions.assertEquals(savedBooking.getName(), result.getName());
		Assertions.assertEquals(savedBooking.getCustomerId(), result.getCustomerId());
		Assertions.assertEquals(savedBooking.getDateTime(), result.getDateTime());
	}

	@Test

	void testAddBooking_InvalidProductId_ExceptionThrown() {
		// Arrange
		BookingDto bookingDto = new BookingDto();
		bookingDto.setProductId(999);
		bookingDto.setCustomerId(1);
		bookingDto.setDateTime(null);

		ResponseEntity<ProductDto> productResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(ProductDto.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
		// Act and Assert
		Assertions.assertThrows(InvalidProductIdException.class, () -> {
			bookingService.addBooking(bookingDto);
		});
	}

	@Test
	void testAddBooking_CustomerNotFound_ExceptionThrown() {
		// Arrange
		BookingDto bookingDto = new BookingDto();
		bookingDto.setProductId(1);
		bookingDto.setCustomerId(999);
		bookingDto.setDateTime(null);

		ProductDto productDto = new ProductDto();
		productDto.setProductId(1);
		productDto.setCharges(100);
		productDto.setCategory("Category");
		productDto.setSize("Size");
		productDto.setName("Product Name");

		ResponseEntity<ProductDto> productResponse = new ResponseEntity<>(productDto, HttpStatus.OK);
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(ProductDto.class)))
				.thenReturn(productResponse);

		ResponseEntity<CustomerDto> customerResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(CustomerDto.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

		// Act and Assert
		Assertions.assertThrows(CustomerNotFoundException.class, () -> {
			bookingService.addBooking(bookingDto);
		});
	}

	@Test
	void testViewAllBookings_ReturnsBookings_Success() {
		// Arrange
		List<Booking> expectedBookings = new ArrayList<>();
		expectedBookings.add(new Booking(1, 1, 100.0f));
		expectedBookings.add(new Booking(2, 2, 200.0f));

		Mockito.when(bookingRepo.findAll()).thenReturn(expectedBookings);

		// Act
		Iterable<Booking> result = bookingService.viewAllBookings();

		// Assert
		Assertions.assertNotNull(result);
		Assertions.assertEquals(expectedBookings.size(), ((List<Booking>) result).size());
		Assertions.assertEquals(expectedBookings.get(0).getBookingId(), ((List<Booking>) result).get(0).getBookingId());
		Assertions.assertEquals(expectedBookings.get(1).getBookingId(), ((List<Booking>) result).get(1).getBookingId());
		// Add more assertions if necessary
	}

	@Test
	void testDeleteBookingById_NonExistingBookingId_ThrowsException() {
		// Arrange
		int bookingId = 1;

		Mockito.when(bookingRepo.findById(bookingId)).thenReturn(Optional.empty());

		// Act & Assert
		Assertions.assertThrows(BookingIdNotFoundException.class, () -> {
			bookingService.deleteBookingById(bookingId);
		});
	}

	@Test
	void testCalculateBill_BookingsExist_ReturnsTotalBillWithTax() {
		// Arrange
		int customerId = 1;
		List<Booking> bookings = new ArrayList<>();
		Booking booking1 = new Booking();
		booking1.setCharges(100.0f);
		Booking booking2 = new Booking();
		booking2.setCharges(200.0f);
		bookings.add(booking1);
		bookings.add(booking2);

		Mockito.when(bookingRepo.findAllByCustomerId(customerId)).thenReturn(bookings);

		// Act
		double result = bookingService.calculateBill(customerId);

		// Assert
		double expectedSum = 100.0 + 200.0;
		double expectedTax = expectedSum * 0.025 * 2; // 2.5% CGST + 2.5% SGST
		double expectedTotal = expectedSum + expectedTax;
		Assertions.assertEquals(expectedTotal, result);
	}

	@Test
	void testCalculateBill_NoBookingsExist_ThrowsException() {
		// Arrange
		int customerId = 1;
		List<Booking> emptyBookings = new ArrayList<>();

		Mockito.when(bookingRepo.findAllByCustomerId(customerId)).thenReturn(emptyBookings);

		// Act & Assert
		Assertions.assertThrows(NoProductsBookedException.class, () -> {
			bookingService.calculateBill(customerId);
		});
	}

	@Test
	void testGetBookingById_BookingExists_ReturnsResponseDtoWithProductAndCustomer() {
		// Arrange
		int bookingId = 1;
		Booking booking = new Booking();
		booking.setProductId(10);
		booking.setCustomerId(20);

		Mockito.when(bookingRepo.findById(bookingId)).thenReturn(Optional.of(booking));

		ProductDto productDto = new ProductDto();
		productDto.setProductId(10);
		productDto.setName("Product 1");

		CustomerDto customerDto = new CustomerDto();
		customerDto.setCustomerId(20);
		customerDto.setCustomerName("Customer 1");

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(ProductDto.class)))
				.thenReturn(new ResponseEntity<>(productDto, HttpStatus.OK));

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(CustomerDto.class)))
				.thenReturn(new ResponseEntity<>(customerDto, HttpStatus.OK));

		// Act
		ResponseDto responseDto = bookingService.getBookingById(bookingId);

		// Assert
		Assertions.assertEquals(productDto, responseDto.getProduct());
		Assertions.assertEquals(customerDto, responseDto.getCustomer());
	}

	@Test
	void testGetBookingById_BookingDoesNotExist_ThrowsException() {
		// Arrange
		int bookingId = 1;

		Mockito.when(bookingRepo.findById(bookingId)).thenReturn(Optional.empty());

		// Act & Assert
		Assertions.assertThrows(BookingIdNotFoundException.class, () -> {
			bookingService.getBookingById(bookingId);
		});
	}

	@Test
	void testGetBookingById_InvalidProductId_ThrowsException() {
		// Arrange
		int bookingId = 1;
		Booking booking = new Booking();
		booking.setProductId(10);
		booking.setCustomerId(20);

		Mockito.when(bookingRepo.findById(bookingId)).thenReturn(Optional.of(booking));

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(ProductDto.class)))
				.thenThrow(new InvalidProductIdException("Product not found"));

		// Act & Assert
		Assertions.assertThrows(InvalidProductIdException.class, () -> {
			bookingService.getBookingById(bookingId);
		});
	}

	@Test
	void testGetBookingById_InvalidCustomerId_ThrowsException() {
		// Arrange
		int bookingId = 1;
		Booking booking = new Booking();
		booking.setProductId(10);
		booking.setCustomerId(20);

		Mockito.when(bookingRepo.findById(bookingId)).thenReturn(Optional.of(booking));

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(ProductDto.class)))
				.thenReturn(new ResponseEntity<>(new ProductDto(), HttpStatus.OK));

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(CustomerDto.class)))
				.thenThrow(new CustomerNotFoundException("Customer not found"));

		// Act & Assert
		Assertions.assertThrows(CustomerNotFoundException.class, () -> {
			bookingService.getBookingById(bookingId);
		});
	}

}
