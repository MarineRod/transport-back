package fr.diginamic.gestion_transport.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.diginamic.gestion_transport.dto.CarpoolingDTO;
import fr.diginamic.gestion_transport.dto.MailSendingSettingsDTO;
import fr.diginamic.gestion_transport.dto.VehicleDTO;
import fr.diginamic.gestion_transport.entites.Carpooling;
import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.repositories.CarpoolingRepository;
import fr.diginamic.gestion_transport.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CarpoolingServiceTest {

	@InjectMocks
	private CarpoolingService testedObject;

	@Mock
	private CarpoolingRepository carpoolingRepositoryMock;

	@Mock
	private UserService userServiceMock;

	@Mock
	private EmailService emailServiceMock;

	@Mock
	private UserRepository userRepositoryMock;

	@BeforeEach
	void setUp() {

		testedObject = new CarpoolingService(carpoolingRepositoryMock, userServiceMock, userRepositoryMock,
				emailServiceMock);
	}

	@Test
	void testSaveCarpooling() throws Exception {

		VehicleDTO vehicle = new VehicleDTO();
		vehicle.setNbSeats(4);

		CarpoolingDTO dto = new CarpoolingDTO();
		dto.setVehicle(vehicle);
		dto.setDuration(90);
		dto.setDistance(300.0);
		dto.setDateTimeStart(LocalDateTime.now().plusDays(1));
		dto.setDepartureAddress("10 rue de Paris");
		dto.setArrivalAddress("Lyon centre");
		dto.setId(null);

		Carpooling expectedEntity = new Carpooling();
		expectedEntity.setDepartureAddress(dto.getDepartureAddress());
		expectedEntity.setArrivalAddress(dto.getArrivalAddress());

		when(carpoolingRepositoryMock.save(any(Carpooling.class))).thenReturn(expectedEntity);

		Carpooling result = testedObject.saveCarpooling(dto);

		assertNotNull(result);
		assertEquals("10 rue de Paris", result.getDepartureAddress());
		assertEquals("Lyon centre", result.getArrivalAddress());
		verify(carpoolingRepositoryMock).save(any(Carpooling.class));

	}

	@Test
	void testSaveCarpooling_whenVehicleIsNull_thenThrowException() {

		CarpoolingDTO dto = new CarpoolingDTO();
		dto.setVehicle(null);
		dto.setDuration(90);
		dto.setDistance(300.0);
		dto.setDateTimeStart(LocalDateTime.now().plusDays(1));
		dto.setDepartureAddress("Paris");
		dto.setArrivalAddress("Lyon");

		assertThrows(Exception.class, () -> testedObject.saveCarpooling(dto));

	}

	@Test
	void testDeleteCarpooling() throws Exception {
		Integer carpoolingId = 1;
		Long organisatorId = 123L;

		User organisator = new User();
		organisator.setId(organisatorId);
		organisator.setUsername("user@example.com");

		User user2 = new User();
		user2.setId(12L);
		user2.setUsername("user2@example.com");

		User user3 = new User();
		user3.setId(13L);
		user3.setUsername("user3@example.com");

		Carpooling carpooling = new Carpooling();
		carpooling.setId(carpoolingId);
		carpooling.setOrganisator(organisator);
		carpooling.setDateTimeStart(LocalDateTime.now());

		Set<User> users = new HashSet<>();
		users.add(user2);
		users.add(user3);
		carpooling.setUsers(users);

		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.of(carpooling));
		when(carpoolingRepositoryMock.deleteByIdAndUserId(carpoolingId, organisatorId)).thenReturn(1);

		testedObject.deleteCarpooling(carpoolingId, organisatorId);
		verify(carpoolingRepositoryMock).deleteByIdAndUserId(carpoolingId, organisatorId);

		ArgumentCaptor<MailSendingSettingsDTO> mailCaptor = ArgumentCaptor.forClass(MailSendingSettingsDTO.class);
		verify(emailServiceMock, times(2)).sendHtmlMail(mailCaptor.capture());

	}

	@Test
	void testDeleteCarpooling_CarpoolingNotFound() {
		Integer carpoolingId = 1;
		Long userId = 123L;

		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.empty());

		assertThrows(Exception.class, () -> {
			testedObject.deleteCarpooling(carpoolingId, userId);
		});

	}

	@Test
	void testDeleteCarpooling_NoUsers_NoEmailSent() throws Exception {
		Integer carpoolingId = 1;
		Long organisatorId = 123L;

		User organisator = new User();
		organisator.setId(organisatorId);
		organisator.setUsername("user@example.com");

		Set<User> users = Collections.emptySet();

		Carpooling carpooling = new Carpooling();
		carpooling.setId(carpoolingId);
		carpooling.setOrganisator(organisator);
		carpooling.setDateTimeStart(LocalDateTime.now());

		carpooling.setUsers(users);

		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.of(carpooling));
		when(carpoolingRepositoryMock.deleteByIdAndUserId(carpoolingId, organisatorId)).thenReturn(1);

		testedObject.deleteCarpooling(carpoolingId, organisatorId);

		verify(emailServiceMock, never()).sendHtmlMail(any());
	}

	@Test
	void testGetCarpooling() throws Exception {
		Integer carpoolingId = 1;
		Carpooling carpooling = new Carpooling();
		carpooling.setId(carpoolingId);

		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.of(carpooling));

		Carpooling result = testedObject.getCarpooling(carpoolingId);

		assertEquals(carpoolingId, result.getId());
	}

	@Test
	void testGetCarpooling_NotFound() {
		Integer carpoolingId = 42;

		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.empty());

		assertThrows(Exception.class, () -> {
			testedObject.getCarpooling(carpoolingId);
		});

	}

	@Test
	void testGetCarpoolingParticipantList() throws Exception {
		Integer carpoolingId = 1;

		User user1 = new User();
		user1.setId(10L);
		user1.setUsername("user1@example.com");

		User user2 = new User();
		user2.setId(20L);
		user2.setUsername("user2@example.com");

		Set<User> users = new HashSet<>(Set.of(user1, user2));

		Carpooling carpooling = new Carpooling();
		carpooling.setId(carpoolingId);
		carpooling.setUsers(users);

		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.of(carpooling));

		Set<User> result = testedObject.getCarpoolingParticipantList(carpoolingId);

		assertEquals(2, result.size());
		assertTrue(result.contains(user1));
		assertTrue(result.contains(user2));
	}

	@Test
	void testGetCarpoolingParticipantList_NotFound() throws Exception {
		Integer carpoolingId = 42;
		User organisator = new User();
		organisator.setId(10L);
		organisator.setUsername("user@example.com");
		Carpooling carpooling = new Carpooling();
		carpooling.setId(carpoolingId);
		carpooling.setOrganisator(organisator);
		carpooling.setDateTimeStart(LocalDateTime.now());
		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.of(carpooling));
		Set<User> result = testedObject.getCarpoolingParticipantList(carpoolingId);

		assertNull(result);
	}

	@Test
	void testGetUserBookings_NotArchived() throws Exception {
		User connectedUser = new User();
		connectedUser.setId(1L);

		Carpooling carpooling1 = new Carpooling();
		carpooling1.setId(12);
		carpooling1.setOrganisator(connectedUser);
		carpooling1.setDateTimeStart(LocalDateTime.now());

		Carpooling carpooling2 = new Carpooling();
		carpooling2.setId(12);
		carpooling2.setOrganisator(connectedUser);
		carpooling2.setDateTimeStart(LocalDateTime.now().plusDays(2));

		List<Carpooling> expectedList = List.of(carpooling1, carpooling2);

		when(userServiceMock.getConnectedUser()).thenReturn(connectedUser);
		when(carpoolingRepositoryMock.findCarpoolingByUsersContainsAndDateTimeStartAfter(eq(connectedUser),
				any(LocalDateTime.class))).thenReturn(expectedList);

		List<Carpooling> result = testedObject.getUserBookings(false);

		assertEquals(expectedList, result);
	}

	@Test
	void testGetUserBookings_Archived() throws Exception {
		User connectedUser = new User();
		connectedUser.setId(1L);

		Carpooling carpooling1 = new Carpooling();
		carpooling1.setId(12);
		carpooling1.setOrganisator(connectedUser);
		carpooling1.setDateTimeStart(LocalDateTime.now().plusDays(3));

		Carpooling carpooling2 = new Carpooling();
		carpooling2.setId(13);
		carpooling2.setOrganisator(connectedUser);
		carpooling2.setDateTimeStart(LocalDateTime.now().minusDays(2));

		List<Carpooling> expectedList = List.of(carpooling2);
		when(userServiceMock.getConnectedUser()).thenReturn(connectedUser);

		when(carpoolingRepositoryMock.findCarpoolingByUsersContainsAndDateTimeStartBefore(eq(connectedUser),
				argThat(date -> !date.isAfter(LocalDateTime.now())))).thenReturn(expectedList);

		List<Carpooling> result = testedObject.getUserBookings(true);

		assertEquals(1, result.size());
		assertTrue(result.contains(carpooling2));
		assertFalse(result.contains(carpooling1));

	}

	@Test
	void testGetNbPlacesRemaining_NormalCase() {
		Carpooling carpooling = new Carpooling();
		carpooling.setNbSeats(5);

		User u1 = new User();
		User u2 = new User();
		carpooling.setUsers(new HashSet<>(Set.of(u1, u2)));

		Integer remaining = testedObject.getNbPlacesRemaining(carpooling);
		assertEquals(3, remaining);
	}

	@Test
	void testGetNbPlacesRemaining_Full() {
		Carpooling carpooling = new Carpooling();
		carpooling.setNbSeats(2);

		User u1 = new User();
		User u2 = new User();
		carpooling.setUsers(new HashSet<>(Set.of(u1, u2)));

		Integer remaining = testedObject.getNbPlacesRemaining(carpooling);
		assertEquals(0, remaining);
	}

	@Test
	void testCancelUserBooking() {
		Integer carpoolingId = 1;

		User user = new User();
		user.setId(100L);
		Set<Carpooling> userBookings = new HashSet<>();
		user.setCarpoolings(userBookings);

		Carpooling carpooling = new Carpooling();
		carpooling.setId(carpoolingId);
		Set<User> participants = new HashSet<>();
		participants.add(user);
		carpooling.setUsers(participants);

		userBookings.add(carpooling);

		when(userServiceMock.getConnectedUser()).thenReturn(user);
		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.of(carpooling));
		when(carpoolingRepositoryMock.save(any(Carpooling.class))).thenReturn(carpooling);
		when(userRepositoryMock.save(any(User.class))).thenReturn(user);

		testedObject.cancelUserBooking(carpoolingId);

		assertFalse(carpooling.getUsers().contains(user));
		assertFalse(user.getCarpoolings().contains(carpooling));

	}

	@Test
	void testSaveUserBooking() {
		Integer carpoolingId = 1;

		User user = new User();
		user.setId(101L);
		user.setCarpoolings(new HashSet<>());

		Carpooling carpooling = new Carpooling();
		carpooling.setId(carpoolingId);
		carpooling.setUsers(new HashSet<>());

		when(userServiceMock.getConnectedUser()).thenReturn(user);
		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.of(carpooling));
		when(carpoolingRepositoryMock.save(any(Carpooling.class))).thenReturn(carpooling);
		when(userRepositoryMock.save(any(User.class))).thenReturn(user);

		testedObject.saveUserBooking(carpoolingId);

		assertTrue(carpooling.getUsers().contains(user));
		assertTrue(user.getCarpoolings().contains(carpooling));
	}

	@Test
	void testGetAllOrganisatorCarpooling_NotArchived() throws Exception {
		User user = new User();
		user.setId(1L);

		Carpooling future1 = new Carpooling();
		future1.setId(10);
		future1.setDateTimeStart(LocalDateTime.now().plusDays(2));

		Carpooling future2 = new Carpooling();
		future2.setId(11);
		future2.setDateTimeStart(LocalDateTime.now().plusDays(3));

		when(userServiceMock.getConnectedUser()).thenReturn(user);
		when(carpoolingRepositoryMock.findAllByOrganisatorIdAndDateTimeStartAfter(eq(user.getId()),
				any(LocalDateTime.class))).thenReturn(List.of(future1, future2));

		List<Carpooling> result = testedObject.getAllOrganisatorCarpooling(false);

		assertEquals(2, result.size());
		assertTrue(result.contains(future1));
		assertTrue(result.contains(future2));
	}

	@Test
	void testGetAllOrganisatorCarpooling_Archived() throws Exception {
		User user = new User();
		user.setId(2L);

		Carpooling past = new Carpooling();
		past.setId(20);
		past.setDateTimeStart(LocalDateTime.now().minusDays(3));

		when(userServiceMock.getConnectedUser()).thenReturn(user);
		when(carpoolingRepositoryMock.findAllByOrganisatorIdAndDateTimeStartBefore(eq(user.getId()),
				any(LocalDateTime.class))).thenReturn(List.of(past));

		List<Carpooling> result = testedObject.getAllOrganisatorCarpooling(true);

		assertEquals(1, result.size());
		assertEquals(past, result.get(0));
	}

	@Test
	void testSearch_WithAllCriteria() throws Exception {
		String departure = "Paris";
		String arrival = "Lyon";
		LocalDate date = LocalDate.of(2025, 8, 10);

		Carpooling carpooling = new Carpooling();
		carpooling.setId(1);

		LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
		LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

		when(carpoolingRepositoryMock
				.findAllByDepartureAddressContainingIgnoreCaseOrArrivalAddressContainingIgnoreCaseOrDateTimeStartBetween(
						departure, arrival, start, end))
				.thenReturn(List.of(carpooling));

		List<Carpooling> result = testedObject.search(departure, arrival, date);

		assertEquals(1, result.size());
		assertEquals(carpooling, result.get(0));
	}

	@Test
	void testSearch_WithNoCriteria_ShouldThrowException() {
		String departure = null;
		String arrival = null;
		LocalDate date = null;

		assertThrows(Exception.class, () -> {
			testedObject.search(departure, arrival, date);
		});

	}

	@Test
	void testHasUserBooked_UserHasBooked_ReturnsTrue() {
		Integer carpoolingId = 1;
		User user = new User();
		user.setId(1L);

		Carpooling carpooling = new Carpooling();
		carpooling.setId(carpoolingId);
		carpooling.setUsers(Set.of(user));

		when(userServiceMock.getConnectedUser()).thenReturn(user);
		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.of(carpooling));

		Boolean result = testedObject.hasUserBooked(carpoolingId);

		assertTrue(result);
	}

	@Test
	void testHasUserBooked_CarpoolingNotFound_ThrowsException() {
		Integer carpoolingId = 1;
		User user = new User();
		user.setId(1L);

		when(userServiceMock.getConnectedUser()).thenReturn(user);
		when(carpoolingRepositoryMock.findById(carpoolingId)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> {
			testedObject.hasUserBooked(carpoolingId);
		});

	}

}
