package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.entites.ServiceVehicle;
import fr.diginamic.gestion_transport.repositories.ServiceVehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceVehicleService {

    private final Logger LOG = LoggerFactory.getLogger(ServiceVehicleService.class);

    private final ServiceVehicleRepository serviceVehicleRepository;

    public ServiceVehicleService(ServiceVehicleRepository serviceVehicleRepository) {
        this.serviceVehicleRepository = serviceVehicleRepository;
    }

    public List<ServiceVehicle> getAllVehicleService() throws Exception {
        try {
            return this.serviceVehicleRepository.findAll();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de récupérer les véhicules de service");
        }
    }

    public ServiceVehicle getServiceVehicleByLicensePlat(String licensePlateNumber) throws Exception {
        try {
            ServiceVehicle serviceVehicle = this.serviceVehicleRepository.findByLicensePlateNumber(licensePlateNumber);
            if (serviceVehicle == null)
                throw new Exception("Impossible de trouver le véhicule de service");
            return serviceVehicle;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de récupérer le véhicule de service");
        }
    }

    public ServiceVehicle saveServiceVehicle(ServiceVehicle serviceVehicle) throws Exception {
        try {
            serviceVehicle.setLicensePlateNumber(serviceVehicle.getLicensePlateNumber().toUpperCase());
            return this.serviceVehicleRepository.save(serviceVehicle);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de sauvegarder le véhicule de service");
        }
    }

    public void deleteServiceVehicle(String licensePlateNumber) throws Exception {
        try {
            this.serviceVehicleRepository.deleteByLicensePlateNumber(licensePlateNumber);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de supprimer le véhicule de service");
        }
    }

}
