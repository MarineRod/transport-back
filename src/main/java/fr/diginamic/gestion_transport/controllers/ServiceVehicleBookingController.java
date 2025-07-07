package fr.diginamic.gestion_transport.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.diginamic.gestion_transport.dto.ServiceVehicleBookingDTO;
import fr.diginamic.gestion_transport.dto.UserDTO;
import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.service.SerVehicleBookingService;
import fr.diginamic.gestion_transport.service.UserService;

@RestController
@RequestMapping("/api/service-vehicle-bookings")
public class ServiceVehicleBookingController {

	private final UserService userService;
	private final SerVehicleBookingService bookingService;

	public ServiceVehicleBookingController(UserService userService, SerVehicleBookingService bookingService) {
		this.userService = userService;
		this.bookingService = bookingService;
	}

	@GetMapping
	public ResponseEntity<List<ServiceVehicleBookingDTO>> getAllBookings() {
		List<ServiceVehicleBookingDTO> bookings = bookingService.getAllBookings();
		return new ResponseEntity<>(bookings, HttpStatus.OK);
	}

	@GetMapping("/vehicle/{vehicleId}")
	public ResponseEntity<List<ServiceVehicleBookingDTO>> getBookingsByVehicleId(@PathVariable String vehicleId) {
		List<ServiceVehicleBookingDTO> bookings = bookingService.getBookingsByServiceVehiculeId(vehicleId);
		return new ResponseEntity<>(bookings, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ServiceVehicleBookingDTO> getBookingByBookingId(@PathVariable Integer id) {
		ServiceVehicleBookingDTO booking = bookingService.getBookingByBookingId(id);
		return new ResponseEntity<>(booking, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<ServiceVehicleBookingDTO> createBooking(@RequestBody ServiceVehicleBookingDTO bookingDto) {
		User user = this.userService.getConnectedUser();
		UserDTO userDto = new UserDTO();
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		bookingDto.setUser(userDto);
		ServiceVehicleBookingDTO createdBooking = bookingService.createBooking(bookingDto);
		return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ServiceVehicleBookingDTO> updateBooking(@PathVariable Integer id,
			@RequestBody ServiceVehicleBookingDTO bookingDto) throws Exception {
		ServiceVehicleBookingDTO updatedBooking = bookingService.updateBooking(id, bookingDto);
		return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
	}

	@GetMapping("user-booking")
	public ResponseEntity<List<ServiceVehicleBookingDTO>> getUserBookings(@RequestParam Boolean isArchived)
			throws Exception {
		List<ServiceVehicleBookingDTO> bookings = bookingService.getUserBookings(isArchived);
		return new ResponseEntity<>(bookings, HttpStatus.OK);

	}

	@DeleteMapping("/{id}")
	public void deleteServiceVehicleBooking(@PathVariable Integer id) {
		User user = this.userService.getConnectedUser();
		this.bookingService.deleteBooking(id, user.getId());
	}

}
