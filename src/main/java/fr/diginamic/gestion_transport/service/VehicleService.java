package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.dto.VehicleDTO;
import fr.diginamic.gestion_transport.entites.Vehicle;
import fr.diginamic.gestion_transport.exception.VehicleGetListException;
import fr.diginamic.gestion_transport.exception.VehicleSaveException;
import fr.diginamic.gestion_transport.repositories.VehicleRepository;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final Logger logger = LoggerFactory.getLogger(VehicleService.class);
    private final ModelMapper mapper = ModelMapperCfg.getInstance();
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public Vehicle save(VehicleDTO vehicleToSave) throws VehicleSaveException {
        try {
            Vehicle vehicle = mapper.map(vehicleToSave, Vehicle.class);
            return this.vehicleRepository.save(vehicle);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new VehicleSaveException("Impossible de sauvegarder le véhicule");
        }
    }

    public List<Vehicle> getAllVehicles(Long userId) throws VehicleGetListException {
        try {
            return this.vehicleRepository.findAllByUserId(userId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new VehicleGetListException("Impossible de récupérer les véhicules");
        }
    }
}
