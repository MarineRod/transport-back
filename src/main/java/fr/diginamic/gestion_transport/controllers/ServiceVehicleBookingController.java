package fr.diginamic.gestion_transport.controllers;

import java.util.List;
import java.util.Map;

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
import fr.diginamic.gestion_transport.exception.BookingConflictException;
import fr.diginamic.gestion_transport.service.SerVehicleBookingService;
import fr.diginamic.gestion_transport.service.UserService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/service-vehicle-bookings")
public class ServiceVehicleBookingController {

	private static final String MESSAGE_KEY = "message";
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
	public ResponseEntity<Map<String, Object>> createBooking(@RequestBody ServiceVehicleBookingDTO bookingDto) {
		try {
			User user = this.userService.getConnectedUser();
			UserDTO userDto = new UserDTO();
			userDto.setId(user.getId());
			userDto.setUsername(user.getUsername());
			bookingDto.setUser(userDto);

			ServiceVehicleBookingDTO createdBooking = bookingService.createBooking(bookingDto);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of(MESSAGE_KEY, "Réservation créée avec succès", "data", createdBooking));

		} catch (BookingConflictException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateBooking(@PathVariable Integer id,
			@RequestBody ServiceVehicleBookingDTO bookingDto) {

		try {
			bookingDto.setId(id);
			ServiceVehicleBookingDTO updatedBooking = bookingService.updateBooking(id, bookingDto);
			return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Réservation mise à jour avec succès", "data", updatedBooking));

		} catch (BookingConflictException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));

		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
		}
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
