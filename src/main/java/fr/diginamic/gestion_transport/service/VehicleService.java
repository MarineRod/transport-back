package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.dto.VehicleDTO;
import fr.diginamic.gestion_transport.entites.Vehicle;
import fr.diginamic.gestion_transport.repositories.VehicleRepository;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final Logger LOG = LoggerFactory.getLogger(VehicleService.class);
    private final ModelMapper mapper = ModelMapperCfg.getInstance();
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle save(Vehicle vehicleToSave) throws Exception {
        try {
            Vehicle vehicle = mapper.map(vehicleToSave, Vehicle.class);
            return this.vehicleRepository.save(vehicle);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de sauvegarder le véhicule");
        }
    }

    public Vehicle save(VehicleDTO vehicleToSave) throws Exception {
        try {
            Vehicle vehicle = mapper.map(vehicleToSave, Vehicle.class);
            return this.vehicleRepository.save(vehicle);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de sauvegarder le véhicule");
        }
    }

    public List<Vehicle> getAllVehicles(Long userId) throws Exception {
        try {
            return this.vehicleRepository.findAllByUserId(userId);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de récupérer les véhicules");
        }
    }
}
