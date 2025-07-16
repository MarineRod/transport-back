package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.entites.ServiceVehicle;
import fr.diginamic.gestion_transport.entites.ServiceVehicleBooking;
import fr.diginamic.gestion_transport.repositories.ServiceVehicleBookingRepository;
import fr.diginamic.gestion_transport.repositories.ServiceVehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceVehicleServiceTest {

    @Mock
    private ServiceVehicleRepository serviceVehicleRepository;

    @Mock
    private ServiceVehicleBookingRepository serviceVehicleBookingRepository;


    private ServiceVehicleService serviceVehicleService;
    private ServiceVehicle serviceVehicle;
    private ServiceVehicleBooking serviceVehicleBooking;

    @BeforeEach
    void setUp() {
        serviceVehicleService = new ServiceVehicleService(serviceVehicleRepository, serviceVehicleBookingRepository);
        serviceVehicle = new ServiceVehicle();
        serviceVehicle.setLicensePlateNumber("AB123CD");

        serviceVehicleBooking = new ServiceVehicleBooking();
        serviceVehicleBooking.setServiceVehicle(serviceVehicle);
    }

    @Test
    void getAllVehicleService_ShouldReturnListOfVehicles_WhenRepositoryReturnsData() throws Exception {
        // Given
        List<ServiceVehicle> expectedVehicles = Arrays.asList(serviceVehicle);
        when(serviceVehicleRepository.findAll()).thenReturn(expectedVehicles);

        // When
        List<ServiceVehicle> result = serviceVehicleService.getAllVehicleService();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(serviceVehicle, result.get(0));
        verify(serviceVehicleRepository).findAll();
    }

    @Test
    void getAllVehicleService_ShouldThrowException_WhenRepositoryThrowsException() {
        // Given
        when(serviceVehicleRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            serviceVehicleService.getAllVehicleService();
        });
        assertEquals("Impossible de récupérer les véhicules de service", exception.getMessage());
        verify(serviceVehicleRepository).findAll();
    }

    @Test
    void getServiceVehicleByLicensePlat_ShouldReturnVehicle_WhenVehicleExists() throws Exception {
        // Given
        String licensePlate = "AB123CD";
        when(serviceVehicleRepository.findByLicensePlateNumber(licensePlate)).thenReturn(serviceVehicle);

        // When
        ServiceVehicle result = serviceVehicleService.getServiceVehicleByLicensePlat(licensePlate);

        // Then
        assertNotNull(result);
        assertEquals(serviceVehicle, result);
        verify(serviceVehicleRepository).findByLicensePlateNumber(licensePlate);
    }

    @Test
    void getServiceVehicleByLicensePlat_ShouldThrowException_WhenVehicleNotFound() {
        // Given
        String licensePlate = "NOTFOUND";
        when(serviceVehicleRepository.findByLicensePlateNumber(licensePlate)).thenReturn(null);

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            serviceVehicleService.getServiceVehicleByLicensePlat(licensePlate);
        });
        assertEquals("Impossible de récupérer le véhicule de service", exception.getMessage());
        verify(serviceVehicleRepository).findByLicensePlateNumber(licensePlate);
    }

    @Test
    void saveServiceVehicle_ShouldSaveVehicleWithUppercaseLicensePlate_WhenValidVehicle() throws Exception {
        // Given
        ServiceVehicle vehicleToSave = new ServiceVehicle();
        vehicleToSave.setLicensePlateNumber("ab123cd");

        ServiceVehicle savedVehicle = new ServiceVehicle();
        savedVehicle.setLicensePlateNumber("AB123CD");

        when(serviceVehicleRepository.save(any(ServiceVehicle.class))).thenReturn(savedVehicle);

        // When
        ServiceVehicle result = serviceVehicleService.saveServiceVehicle(vehicleToSave);

        // Then
        assertNotNull(result);
        assertEquals("AB123CD", result.getLicensePlateNumber());
        assertEquals("AB123CD", vehicleToSave.getLicensePlateNumber());
        verify(serviceVehicleRepository).save(vehicleToSave);
    }

    @Test
    void saveServiceVehicle_ShouldThrowException_WhenRepositoryThrowsException() {
        // Given
        ServiceVehicle vehicleToSave = new ServiceVehicle();
        vehicleToSave.setLicensePlateNumber("AB123CD");
        when(serviceVehicleRepository.save(any(ServiceVehicle.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            serviceVehicleService.saveServiceVehicle(vehicleToSave);
        });
        assertEquals("Impossible de sauvegarder le véhicule de service", exception.getMessage());
        verify(serviceVehicleRepository).save(vehicleToSave);
    }

    @Test
    void deleteServiceVehicle_ShouldDeleteVehicle_WhenNoBookingsExist(){
        // Given
        String licensePlate = "AB123CD";
        when(serviceVehicleBookingRepository.findByServiceVehicle_LicensePlateNumber(licensePlate))
                .thenReturn(Collections.emptyList());

        // When
        assertDoesNotThrow(() -> serviceVehicleService.deleteServiceVehicle(licensePlate));

        // Then
        verify(serviceVehicleBookingRepository).findByServiceVehicle_LicensePlateNumber(licensePlate);
        verify(serviceVehicleRepository).deleteByLicensePlateNumber(licensePlate);
    }

    @Test
    void deleteServiceVehicle_ShouldThrowException_WhenBookingsExist() {
        // Given
        String licensePlate = "AB123CD";
        List<ServiceVehicleBooking> bookings = Arrays.asList(serviceVehicleBooking);
        when(serviceVehicleBookingRepository.findByServiceVehicle_LicensePlateNumber(licensePlate))
                .thenReturn(bookings);

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            serviceVehicleService.deleteServiceVehicle(licensePlate);
        });
        assertEquals("Impossible de supprimer un véhicule de service lié à des réservations", exception.getMessage());
        verify(serviceVehicleBookingRepository).findByServiceVehicle_LicensePlateNumber(licensePlate);
        verify(serviceVehicleRepository, never()).deleteByLicensePlateNumber(anyString());
    }
}