package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.dto.VehicleDTO;
import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.entites.Vehicle;
import fr.diginamic.gestion_transport.enums.BrandEnum;
import fr.diginamic.gestion_transport.repositories.VehicleRepository;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleService vehicleService;

    private ModelMapper mapper;

    private Long userId;

    private List<Vehicle> vehicles;

    @BeforeEach
    void setUp() {
        mapper = ModelMapperCfg.getInstance();
        vehicleService = new VehicleService(vehicleRepository);
        userId = 1L;
        vehicles = this.generateMockVehicleList();
    }

    @Test
    void save_Success() {
    }

    @Test
    void getAllVehicles_ShouldReturnVehicleList() throws Exception {
        when(vehicleRepository.findAllByUserId(userId)).thenReturn(vehicles);
        List<Vehicle> result = vehicleService.getAllVehicles(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(vehicleRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void getAllVehicles_ShouldThrowException() throws Exception {
        when(vehicleRepository.findAllByUserId(2L)).thenThrow(new RuntimeException("DB down"));

        Exception exception = assertThrows(Exception.class, () -> vehicleService.getAllVehicles(userId));
        assertEquals("Impossible de récupérer les véhicules", exception.getMessage());
        verify(vehicleRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void save_ShouldReturnSavedVehicle_WhenValidDTO() throws Exception {
        VehicleDTO dto = new VehicleDTO();
        dto.setBrand(BrandEnum.CITROEN);
        dto.setModel("308");

        Vehicle expectedVehicle = mapper.map(dto, Vehicle.class);
        expectedVehicle.setLicensePlateNumber("AB123CD");

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(expectedVehicle);

        Vehicle savedVehicle = vehicleService.save(dto);

        assertNotNull(savedVehicle);
        assertEquals(BrandEnum.CITROEN, savedVehicle.getBrand());
        assertEquals("308", savedVehicle.getModel());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void save_ShouldThrowException() {
        VehicleDTO dto = new VehicleDTO();
        dto.setBrand(BrandEnum.CITROEN);
        dto.setModel("308");

        when(vehicleRepository.save(any(Vehicle.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(Exception.class, () -> vehicleService.save(dto));
        assertEquals("Impossible de sauvegarder le véhicule", exception.getMessage());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    private List<Vehicle> generateMockVehicleList() {
        User user = new User(1L, "test", "test", true, "John", "Doe");
        return Arrays.asList(
                new Vehicle("AB123CD", "308", 4, BrandEnum.CITROEN, new ArrayList<>(), user),
                new Vehicle("EF123GH", "Model", 4, BrandEnum.CITROEN, new ArrayList<>(), user)
        );
    }
}