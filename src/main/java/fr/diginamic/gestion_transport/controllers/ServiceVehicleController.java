package fr.diginamic.gestion_transport.controllers;

import fr.diginamic.gestion_transport.dto.ServiceVehicleDTO;
import fr.diginamic.gestion_transport.entites.ServiceVehicle;
import fr.diginamic.gestion_transport.service.ServiceVehicleService;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-vehicle")
public class ServiceVehicleController {

    ModelMapper mapper = ModelMapperCfg.getInstance();

    @Autowired
    private ServiceVehicleService serviceVehicleService;

    @GetMapping("/get-all")
    public List<ServiceVehicleDTO> getAll() throws Exception {
        List<ServiceVehicle> serviceVehicles = this.serviceVehicleService.getAllVehicleService();
        return serviceVehicles.stream().map(sv -> mapper.map(sv, ServiceVehicleDTO.class)).toList();

    }

    @GetMapping("/get/{licensePlateNumber}")
    public ServiceVehicleDTO get(@PathVariable String licensePlateNumber) throws Exception {
        ServiceVehicle serviceVehicle = this.serviceVehicleService.getServiceVehicleByLicensePlat(licensePlateNumber);
        return mapper.map(serviceVehicle, ServiceVehicleDTO.class);
    }

    @DeleteMapping("/delete/{licensePlateNumber}")
    public void delete(@PathVariable String licensePlateNumber) throws Exception {
        this.serviceVehicleService.deleteServiceVehicle(licensePlateNumber);
    }

    @PostMapping("/save")
    public ServiceVehicleDTO save(@RequestBody ServiceVehicleDTO serviceVehicleDTO) throws Exception {
        ServiceVehicle serviceVehicle = mapper.map(serviceVehicleDTO, ServiceVehicle.class);
        return mapper.map(this.serviceVehicleService.saveServiceVehicle(serviceVehicle), ServiceVehicleDTO.class);
    }
}
