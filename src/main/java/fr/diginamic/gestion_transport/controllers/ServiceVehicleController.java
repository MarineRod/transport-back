package fr.diginamic.gestion_transport.controllers;

import fr.diginamic.gestion_transport.dto.ServiceVehicleDTO;
import fr.diginamic.gestion_transport.entites.ServiceVehicle;
import fr.diginamic.gestion_transport.service.ServiceVehicleService;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-vehicle")
public class ServiceVehicleController {

    ModelMapper mapper = ModelMapperCfg.getInstance();

    private final ServiceVehicleService serviceVehicleService;

    public ServiceVehicleController(ServiceVehicleService serviceVehicleService) {
        this.serviceVehicleService = serviceVehicleService;
    }

	@GetMapping("")
    public List<ServiceVehicleDTO> getAll() throws Exception {
        List<ServiceVehicle> serviceVehicles = this.serviceVehicleService.getAllVehicleService();
        return serviceVehicles.stream().map(sv -> mapper.map(sv, ServiceVehicleDTO.class)).toList();
    }

    @GetMapping("/{licensePlateNumber}")
    public ServiceVehicleDTO get(@PathVariable String licensePlateNumber) throws Exception {
        ServiceVehicle serviceVehicle = this.serviceVehicleService.getServiceVehicleByLicensePlat(licensePlateNumber);
        return mapper.map(serviceVehicle, ServiceVehicleDTO.class);
    }

    @DeleteMapping("/{licensePlateNumber}")
    public void delete(@PathVariable String licensePlateNumber) throws Exception {
        this.serviceVehicleService.deleteServiceVehicle(licensePlateNumber);
    }

    @PostMapping("")
    public ServiceVehicleDTO save(@RequestBody ServiceVehicleDTO serviceVehicleDTO) throws Exception {
        ServiceVehicle serviceVehicle = mapper.map(serviceVehicleDTO, ServiceVehicle.class);
        return mapper.map(this.serviceVehicleService.saveServiceVehicle(serviceVehicle), ServiceVehicleDTO.class);
    }
}