package fr.diginamic.gestion_transport.controllers;

import fr.diginamic.gestion_transport.dto.ServiceVehicleDTO;
import fr.diginamic.gestion_transport.entites.ServiceVehicle;
import fr.diginamic.gestion_transport.service.ServiceVehicleService;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/service-vehicle")
public class ServiceVehicleController {

    ModelMapper mapper = ModelMapperCfg.getInstance();

    @Autowired
    private ServiceVehicleService serviceVehicleService;

    @GetMapping("/get-all")
    public List<ServiceVehicleDTO> getAll() {
        List<ServiceVehicle> serviceVehicles = this.serviceVehicleService.getAllVehicleService();
        return serviceVehicles.stream().map(sv -> mapper.map(sv, ServiceVehicleDTO.class)).toList();

    }
}
