package fr.diginamic.gestion_transport.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.diginamic.gestion_transport.dto.ServiceVehicleBookingDTO;
import fr.diginamic.gestion_transport.entites.ServiceVehicleBooking;
import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.exception.BookingConflictException;
import fr.diginamic.gestion_transport.exception.InvalidBookingDatesException;
import fr.diginamic.gestion_transport.repositories.ServiceVehicleBookingRepository;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class SerVehicleBookingService {

	private final ModelMapper modelMapper = ModelMapperCfg.getInstance();
	private final Logger logger = LoggerFactory.getLogger(SerVehicleBookingService.class);
	private final ServiceVehicleBookingRepository serviceVehicleBookingRepository;
	private final UserService userService;

	public SerVehicleBookingService(ServiceVehicleBookingRepository serviceVehicleBookingRepository,
			UserService userService) {
		this.serviceVehicleBookingRepository = serviceVehicleBookingRepository;
		this.userService = userService;

	}

	private ServiceVehicleBookingDTO convertToDto(ServiceVehicleBooking booking) {
		modelMapper.typeMap(ServiceVehicleBooking.class, ServiceVehicleBookingDTO.class).addMappings(mapper -> {
			mapper.map(src -> src.getServiceVehicle().getLicensePlateNumber(),
					ServiceVehicleBookingDTO::setLicensePlateNumber);
		});
		return modelMapper.map(booking, ServiceVehicleBookingDTO.class);
	}

	private ServiceVehicleBooking convertToEntity(ServiceVehicleBookingDTO bookingDto) {
		return modelMapper.map(bookingDto, ServiceVehicleBooking.class);
	}

	@Transactional(rollbackOn = Exception.class)
	public ServiceVehicleBookingDTO createBooking(ServiceVehicleBookingDTO bookingDto) {
		ServiceVehicleBooking booking = convertToEntity(bookingDto);
		User connectedUser = userService.getConnectedUser();
		booking.setUser(connectedUser);

		validateBookingDates(booking);
		checkForBookingConflict(booking, null);

		ServiceVehicleBooking savedBooking = serviceVehicleBookingRepository.save(booking);
		return convertToDto(savedBooking);
	}

	@Transactional(rollbackOn = Exception.class)
	public List<ServiceVehicleBookingDTO> getAllBookings() {
		return serviceVehicleBookingRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@Transactional(rollbackOn = Exception.class)
	public List<ServiceVehicleBookingDTO> getBookingsByServiceVehiculeId(String serviceVehiculeId) {
		return serviceVehicleBookingRepository.findByServiceVehicle_LicensePlateNumber(serviceVehiculeId).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	@Transactional(rollbackOn = Exception.class)
	public ServiceVehicleBookingDTO getBookingByBookingId(Integer bookingId) {
		ServiceVehicleBooking booking = serviceVehicleBookingRepository.findById(bookingId)
				.orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'ID : " + bookingId));
		return convertToDto(booking);
	}

	@Transactional(rollbackOn = Exception.class)
	public ServiceVehicleBookingDTO updateBooking(Integer id, ServiceVehicleBookingDTO dto) {

		ServiceVehicleBooking booking = serviceVehicleBookingRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'ID : " + id));

		if (!booking.getId().equals(dto.getId())) {
			throw new IllegalArgumentException("Impossible de modifier la réservation");

		}

		booking.setDateTimeStart(dto.getDateTimeStart());
		booking.setDateTimeEnd(dto.getDateTimeEnd());

		validateBookingDates(booking);
		checkForBookingConflict(booking, booking.getId());
		ServiceVehicleBooking updated = serviceVehicleBookingRepository.save(booking);
		return convertToDto(updated);
	}

	@Transactional(rollbackOn = Exception.class)
	public List<ServiceVehicleBookingDTO> getUserBookings(Boolean isArchived) throws Exception {
		try {
			Long id = userService.getConnectedUser().getId();
			List<ServiceVehicleBooking> bookings;

			if (Boolean.FALSE.equals(isArchived)) {
				bookings = serviceVehicleBookingRepository.findCurrentAndUpcomingBookings(id, LocalDateTime.now());
			} else {
				bookings = serviceVehicleBookingRepository.findPastBookingsFullyEnded(id, LocalDateTime.now());
			}
			return bookings.stream().map(this::convertToDto).toList();

		} catch (Exception e) {
			logger.error("Erreur lors de la récupération des réservations : {}", e.getMessage());
			throw new Exception("Impossible de récupérer la liste des réservations de véhicules de service", e);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteBooking(Integer id, Long userId) {
		int deletedCount = serviceVehicleBookingRepository.deleteByIdAndUserId(id, userId);
		if (deletedCount == 0) {

			throw new EntityNotFoundException("Impossible de supprimer la réservation avec l'ID : " + id);
		}
	}

	// =============================
	// MÉTHODES PRIVÉES
	// =============================

	private void checkForBookingConflict(ServiceVehicleBooking booking, Integer excludeBookingId) {
		String vehicleId = booking.getServiceVehicle().getLicensePlateNumber();
		LocalDateTime start = booking.getDateTimeStart();
		LocalDateTime end = booking.getDateTimeEnd();

		boolean hasConflict = serviceVehicleBookingRepository.existsOverlappingBooking(vehicleId, start, end,
				excludeBookingId);
		if (hasConflict) {
			throw new BookingConflictException(
					"Conflit de réservation : le créneau demandé chevauche une réservation existante.");
		}
	}

	private void validateBookingDates(ServiceVehicleBooking booking) {
		if (booking.getDateTimeStart() == null || booking.getDateTimeEnd() == null) {
			throw new InvalidBookingDatesException("La date de début et la date de fin doivent être renseignées.");
		}

		if (booking.getDateTimeEnd().isBefore(booking.getDateTimeStart())) {
			throw new InvalidBookingDatesException("La date de fin ne peut pas être antérieure à la date de début.");
		}

	}

}
