package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.entites.ServiceVehicle;
import fr.diginamic.gestion_transport.repositories.ServiceVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceVehicleService {

    @Autowired
    private ServiceVehicleRepository serviceVehicleRepository;

    public List<ServiceVehicle> getAllVehicleService() {
        return this.serviceVehicleRepository.findAll();
    }

}
