package fr.diginamic.gestion_transport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.diginamic.gestion_transport.dto.ServiceVehicleBookingDTO;
import fr.diginamic.gestion_transport.dto.UserDTO;
import fr.diginamic.gestion_transport.entites.ServiceVehicle;
import fr.diginamic.gestion_transport.entites.ServiceVehicleBooking;
import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.exception.BookingConflictException;
import fr.diginamic.gestion_transport.exception.InvalidBookingDatesException;
import fr.diginamic.gestion_transport.repositories.ServiceVehicleBookingRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class SerVehicleBookingServiceTest {

	@InjectMocks
	private SerVehicleBookingService testedObject;

	@Mock
	private ServiceVehicleBookingRepository serviceVehicleBookingRepositoryMock;

	@Mock
	private UserService userServiceMock;

	@BeforeEach
	void setUp() {
		testedObject = new SerVehicleBookingService(serviceVehicleBookingRepositoryMock, userServiceMock);
	}

	@Test
	void testCreate() {
		Integer idBooking = 15;
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(2);

		ServiceVehicle serviceVehicle = ServiceVehicle.builder().licensePlateNumber("AB-123-CD").build();

		User userConnected = User.builder().id(89L).build();
		UserDTO userDTO = UserDTO.builder().id(89L).build();

		ServiceVehicleBookingDTO bookingDto = ServiceVehicleBookingDTO.builder().id(idBooking)
				.licensePlateNumber("AB-123-CD").dateTimeStart(start).dateTimeEnd(end).user(userDTO).build();

		ServiceVehicleBooking booking = ServiceVehicleBooking.builder().id(idBooking).serviceVehicle(serviceVehicle)
				.dateTimeStart(start).dateTimeEnd(end).user(userConnected).build();

		when(userServiceMock.getConnectedUser()).thenReturn(userConnected);
		when(serviceVehicleBookingRepositoryMock.save(any(ServiceVehicleBooking.class))).thenReturn(booking);

		ServiceVehicleBookingDTO result = testedObject.createBooking(bookingDto);

		assertThat(result.getId()).isEqualTo(idBooking);
		assertThat(result.getDateTimeStart()).isEqualTo(start);
		assertThat(result.getDateTimeEnd()).isEqualTo(end);
		assertThat(result.getLicensePlateNumber()).isEqualTo("AB-123-CD");
		assertThat(result.getUser().getId()).isEqualTo(userConnected.getId());

		verify(serviceVehicleBookingRepositoryMock).save(any(ServiceVehicleBooking.class));
		verify(userServiceMock).getConnectedUser();
	}

	@Test
	void testCreate_whenDateDendIsNull_thenThrowException() {
		Integer idBooking = 15;
		LocalDateTime start = LocalDateTime.now();

		User userConnected = User.builder().id(89L).build();
		UserDTO userDTO = UserDTO.builder().id(89L).build();

		ServiceVehicleBookingDTO bookingDto = ServiceVehicleBookingDTO.builder().id(idBooking)
				.licensePlateNumber("AB-123-CD").dateTimeStart(start).user(userDTO).build();

		when(userServiceMock.getConnectedUser()).thenReturn(userConnected);
		assertThatThrownBy(() -> testedObject.createBooking(bookingDto))
				.isInstanceOf(InvalidBookingDatesException.class)
				.hasMessageContaining("La date de début et la date de fin doivent être renseignées.");
	}

	@Test
	void testCreate_whenDateEndInferieurDateStart_thenThrowException() {
		Integer idBooking = 15;
		LocalDateTime end = LocalDateTime.now();
		LocalDateTime start = end.plusHours(5);

		User userConnected = User.builder().id(89L).build();
		UserDTO userDTO = UserDTO.builder().id(89L).build();

		ServiceVehicleBookingDTO bookingDto = ServiceVehicleBookingDTO.builder().id(idBooking)
				.licensePlateNumber("AB-123-CD").dateTimeStart(start).dateTimeEnd(end).user(userDTO).build();
		when(userServiceMock.getConnectedUser()).thenReturn(userConnected);
		assertThatThrownBy(() -> testedObject.createBooking(bookingDto))
				.isInstanceOf(InvalidBookingDatesException.class)
				.hasMessageContaining("La date de fin ne peut pas être antérieure à la date de début.");
	}

	@Test
	void testCreate_whenConflictBooking_thenThrowException() {
		Integer idBooking = 15;
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(2);

		User userConnected = User.builder().id(89L).build();
		UserDTO userDTO = UserDTO.builder().id(89L).build();

		ServiceVehicleBookingDTO bookingDto = ServiceVehicleBookingDTO.builder().id(idBooking)
				.licensePlateNumber("AB-123-CD").dateTimeStart(start).dateTimeEnd(end).user(userDTO).build();

		when(userServiceMock.getConnectedUser()).thenReturn(userConnected);
		when(serviceVehicleBookingRepositoryMock.existsOverlappingBooking("AB-123-CD", start, end, null))
				.thenReturn(true);

		assertThatThrownBy(() -> testedObject.createBooking(bookingDto)).isInstanceOf(BookingConflictException.class)
				.hasMessageContaining(
						"Conflit de réservation : le créneau demandé chevauche une réservation existante.");
	}

	// ======================================================================================================================

	@Test
	void testUpdate() {
		Integer idBooking = 15;
		LocalDateTime existingStart = LocalDateTime.now().withHour(10).withMinute(0);
		LocalDateTime existingEnd = existingStart.plusDays(6).withHour(12).withMinute(0);

		LocalDateTime updatedStart = existingEnd.plusWeeks(1).withHour(9).withMinute(0);
		LocalDateTime updatedEnd = updatedStart.plusWeeks(1).withHour(17).withMinute(0);

		ServiceVehicle serviceVehicle = ServiceVehicle.builder().licensePlateNumber("AB-123-CD").build();

		User userConnected = User.builder().id(89L).build();
		UserDTO userDTO = UserDTO.builder().id(89L).build();

		ServiceVehicleBookingDTO updatedDto = ServiceVehicleBookingDTO.builder().id(idBooking)
				.licensePlateNumber("AB-123-CD").dateTimeStart(updatedStart).dateTimeEnd(updatedEnd).user(userDTO)
				.build();

		ServiceVehicleBooking existingBooking = ServiceVehicleBooking.builder().id(idBooking)
				.serviceVehicle(serviceVehicle).dateTimeStart(existingStart).dateTimeEnd(existingEnd)
				.user(userConnected).build();

		when(serviceVehicleBookingRepositoryMock.findById(idBooking)).thenReturn(Optional.of(existingBooking));
		when(serviceVehicleBookingRepositoryMock.save(any(ServiceVehicleBooking.class))).thenReturn(existingBooking);
		ServiceVehicleBookingDTO result = testedObject.updateBooking(idBooking, updatedDto);

		assertThat(result.getId()).isEqualTo(idBooking);
		assertThat(result.getDateTimeStart()).isEqualTo(updatedStart);
		assertThat(result.getDateTimeEnd()).isEqualTo(updatedEnd);
		assertThat(result.getLicensePlateNumber()).isEqualTo("AB-123-CD");

	}

	@Test
	void testUpdate_whenBookingNotFound_thenThrowException() {
		Integer idBooking = 15;
		ServiceVehicleBookingDTO updatedDto = ServiceVehicleBookingDTO.builder().id(idBooking).build();

		when(serviceVehicleBookingRepositoryMock.findById(idBooking)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> testedObject.updateBooking(idBooking, updatedDto))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Réservation non trouvée avec l'ID : 15");
	}

	@Test
	void testUpdate_whenEndDateBeforeStartDate_ThrowException_() {
		Integer idBooking = 15;
		LocalDateTime start = LocalDateTime.now().plusDays(1);
		LocalDateTime end = start.minusHours(2);

		ServiceVehicleBookingDTO dto = ServiceVehicleBookingDTO.builder().id(idBooking).dateTimeStart(start)
				.dateTimeEnd(end).licensePlateNumber("AB-123-CD").build();

		ServiceVehicleBooking existingBooking = ServiceVehicleBooking.builder().id(idBooking)
				.serviceVehicle(ServiceVehicle.builder().licensePlateNumber("AB-123-CD").build())
				.dateTimeStart(LocalDateTime.now()).dateTimeEnd(LocalDateTime.now().plusHours(2))
				.user(User.builder().id(1L).build()).build();

		when(serviceVehicleBookingRepositoryMock.findById(idBooking)).thenReturn(Optional.of(existingBooking));

		assertThatThrownBy(() -> testedObject.updateBooking(idBooking, dto))
				.isInstanceOf(InvalidBookingDatesException.class)
				.hasMessageContaining("La date de fin ne peut pas être antérieure");
	}

	@Test
	void testUpdate_whenConflictBooking_thenThrowException() {
		Integer idBooking = 15;
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(2);

		UserDTO userDTO = UserDTO.builder().id(89L).build();

		ServiceVehicleBookingDTO bookingDto = ServiceVehicleBookingDTO.builder().id(idBooking)
				.licensePlateNumber("AB-123-CD").dateTimeStart(start).dateTimeEnd(end).user(userDTO).build();

		ServiceVehicleBooking existingBooking = ServiceVehicleBooking.builder().id(idBooking)
				.serviceVehicle(ServiceVehicle.builder().licensePlateNumber("AB-123-CD").build())
				.dateTimeStart(LocalDateTime.now()).dateTimeEnd(LocalDateTime.now().plusHours(1))
				.user(User.builder().id(1L).build()).build();

		when(serviceVehicleBookingRepositoryMock.findById(idBooking)).thenReturn(Optional.of(existingBooking));
		when(serviceVehicleBookingRepositoryMock.existsOverlappingBooking("AB-123-CD", start, end, 15))
				.thenReturn(true);

		assertThatThrownBy(() -> testedObject.updateBooking(idBooking, bookingDto))
				.isInstanceOf(BookingConflictException.class).hasMessageContaining(
						"Conflit de réservation : le créneau demandé chevauche une réservation existante.");
	}

	// ======================================================================================================================

	@Test
	void testGet() {
		Integer idBooking = 15;
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(2);

		User userConnected = User.builder().id(89L).build();
		ServiceVehicle serviceVehicle = ServiceVehicle.builder().licensePlateNumber("AB-123-CD").build();

		ServiceVehicleBooking booking = ServiceVehicleBooking.builder().id(idBooking).serviceVehicle(serviceVehicle)
				.dateTimeStart(start).dateTimeEnd(end).user(userConnected).build();

		when(serviceVehicleBookingRepositoryMock.findById(idBooking)).thenReturn(Optional.of(booking));
		ServiceVehicleBookingDTO result = testedObject.getBookingByBookingId(idBooking);

		assertThat(result.getId()).isEqualTo(idBooking);
		assertThat(result.getDateTimeStart()).isEqualTo(start);
		assertThat(result.getDateTimeEnd()).isEqualTo(end);
		assertThat(result.getLicensePlateNumber()).isEqualTo("AB-123-CD");

	}

	@Test
	void testGet_whenBookingNotExists_thenThrowException() {
		Integer idBooking = 15;

		assertThatThrownBy(() -> testedObject.getBookingByBookingId(idBooking))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Réservation non trouvée avec l'ID : 15");

	}

	// ======================================================================================================================

	@Test
	void testGetUserBookings_whenNotArchived_thenReturnCurrentAndUpcomingBookings() throws Exception {
		Integer idBooking = 15;
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(2);

		User userConnected = User.builder().id(89L).build();
		ServiceVehicle serviceVehicle = ServiceVehicle.builder().licensePlateNumber("AB-123-CD").build();

		ServiceVehicleBooking booking = ServiceVehicleBooking.builder().id(idBooking).serviceVehicle(serviceVehicle)
				.dateTimeStart(start).dateTimeEnd(end).user(userConnected).build();
		Boolean isArchived = false;
		when(userServiceMock.getConnectedUser()).thenReturn(userConnected);
		when(serviceVehicleBookingRepositoryMock.findCurrentAndUpcomingBookings(eq(userConnected.getId()), any()))
				.thenReturn(List.of(booking));
		List<ServiceVehicleBookingDTO> result = testedObject.getUserBookings(isArchived);
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getLicensePlateNumber()).isEqualTo("AB-123-CD");

	}

	@Test
	void testGetUserBookings_whenArchived_thenReturnPastBookings() throws Exception {
		Integer idBooking = 15;
		LocalDateTime start = LocalDateTime.now().minusDays(5);
		LocalDateTime end = LocalDateTime.now().minusDays(4);

		User userConnected = User.builder().id(89L).build();
		ServiceVehicle serviceVehicle = ServiceVehicle.builder().licensePlateNumber("AB-123-CD").build();

		ServiceVehicleBooking booking = ServiceVehicleBooking.builder().id(idBooking).serviceVehicle(serviceVehicle)
				.dateTimeStart(start).dateTimeEnd(end).user(userConnected).build();
		Boolean isArchived = true;
		when(userServiceMock.getConnectedUser()).thenReturn(userConnected);
		when(serviceVehicleBookingRepositoryMock.findPastBookingsFullyEnded(eq(userConnected.getId()), any()))
				.thenReturn(List.of(booking));
		List<ServiceVehicleBookingDTO> result = testedObject.getUserBookings(isArchived);
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getLicensePlateNumber()).isEqualTo("AB-123-CD");

	}

	// ======================================================================================================================

	@Test
	void testGetAllBookings() {
		Integer idBooking = 15;
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(2);

		ServiceVehicle serviceVehicle = ServiceVehicle.builder().licensePlateNumber("AB-123-CD").build();

		ServiceVehicleBooking booking = ServiceVehicleBooking.builder().id(idBooking).serviceVehicle(serviceVehicle)
				.dateTimeStart(start).dateTimeEnd(end).build();
		when(serviceVehicleBookingRepositoryMock.findAll()).thenReturn(List.of(booking));
		List<ServiceVehicleBookingDTO> result = testedObject.getAllBookings();
		assertThat(result).hasSize(1);
	}

	// ======================================================================================================================

	@Test
	void testDeleteBooking() {
		Long userId = 89L;
		Integer idBooking = 15;

		when(serviceVehicleBookingRepositoryMock.deleteByIdAndUserId(idBooking, userId)).thenReturn(1);

		testedObject.deleteBooking(idBooking, userId);

		verify(serviceVehicleBookingRepositoryMock, times(1))
				.deleteByIdAndUserId(idBooking, userId);
	}

	@Test
	void testDeleteBooking_whenBookingNotFound_thenThrowException() {
		Integer idBooking = 15;
		Long userId = 89L;

		when(serviceVehicleBookingRepositoryMock.deleteByIdAndUserId(idBooking, userId)).thenReturn(0);

		assertThatThrownBy(() -> testedObject.deleteBooking(idBooking, userId))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Impossible de supprimer la réservation avec l'ID : " + idBooking);
	}

	// ======================================================================================================================

	@Test
	void testGetBookingsByServiceVehicleId() {
		String serviceVehicleId = "AB-123-CD";
		ServiceVehicle serviceVehicle = ServiceVehicle.builder().licensePlateNumber(serviceVehicleId).build();
		ServiceVehicleBooking booking = ServiceVehicleBooking.builder().serviceVehicle(serviceVehicle).build();
		when(serviceVehicleBookingRepositoryMock.findByServiceVehicle_LicensePlateNumber("AB-123-CD"))
				.thenReturn(List.of(booking));
		List<ServiceVehicleBookingDTO> result = testedObject.getBookingsByServiceVehiculeId(serviceVehicleId);
		assertThat(result).hasSize(1);
	}

	@Test
	void testGetBookingsByServiceVehicleId_whenNoBooking_thenReturnEmptyList() {
	    String serviceVehicleId = "AB-123-CD";

	    when(serviceVehicleBookingRepositoryMock.findByServiceVehicle_LicensePlateNumber(serviceVehicleId))
	        .thenReturn(Collections.emptyList());

	    List<ServiceVehicleBookingDTO> result = testedObject.getBookingsByServiceVehiculeId(serviceVehicleId);

	    assertThat(result).isEmpty();  
	}

}
