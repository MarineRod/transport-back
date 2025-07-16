package fr.diginamic.gestion_transport.controllers;

import fr.diginamic.gestion_transport.dto.UserDTO;
import fr.diginamic.gestion_transport.dto.VehicleDTO;
import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.entites.Vehicle;
import fr.diginamic.gestion_transport.service.UserService;
import fr.diginamic.gestion_transport.service.VehicleService;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    private final ModelMapper mapper = ModelMapperCfg.getInstance();

    private final VehicleService vehicleService;

    private final UserService userService;

    public VehicleController(VehicleService vehicleService, UserService userService) {
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    @PostMapping("")
    public VehicleDTO saveVehicle(@RequestBody VehicleDTO vehicle) throws Exception {
        User user = this.userService.getConnectedUser();
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        vehicle.setUser(userDTO);
        Vehicle savedVehicle = this.vehicleService.save(vehicle);
        return mapper.map(savedVehicle, VehicleDTO.class);
    }

    @GetMapping("")
    public List<VehicleDTO> getAllVehicles() throws Exception {
        User user = this.userService.getConnectedUser();
        List<Vehicle> vehicles = this.vehicleService.getAllVehicles(user.getId());
        return vehicles.stream().map(vehicle -> mapper.map(vehicle, VehicleDTO.class)).toList();
    }
}