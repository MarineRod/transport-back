package fr.diginamic.gestion_transport.controllers;

import fr.diginamic.gestion_transport.dto.CarpoolingBookingDTO;
import fr.diginamic.gestion_transport.dto.CarpoolingDTO;
import fr.diginamic.gestion_transport.dto.UserDTO;
import fr.diginamic.gestion_transport.entites.Carpooling;
import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.service.CarpoolingService;
import fr.diginamic.gestion_transport.service.UserService;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carpooling")
public class CarpoolingController {

    ModelMapper mapper = ModelMapperCfg.getInstance();

    private final CarpoolingService carpoolingService;

    private final UserService userService;


    public CarpoolingController(CarpoolingService carpoolingService, UserService userService) {
        this.carpoolingService = carpoolingService;
        this.userService = userService;
    }

    @PostMapping("")
    public CarpoolingDTO saveCarpooling(@RequestBody CarpoolingDTO carpoolingDTO) throws Exception {
        User user = this.userService.getConnectedUser();
        carpoolingDTO.setOrganisator(mapper.map(user, UserDTO.class));
        Carpooling carpooling = this.carpoolingService.saveCarpooling(carpoolingDTO);
        return mapper.map(carpooling, CarpoolingDTO.class);
    }

    @DeleteMapping("/{id}")
    public void deleteCarpooling(@PathVariable Integer id) throws Exception {
        User user = this.userService.getConnectedUser();
        this.carpoolingService.deleteCarpooling(id, user.getId());
    }

    @GetMapping("/{id}")
    public CarpoolingDTO getCarpooling(@PathVariable Integer id) throws Exception {
        Carpooling carpooling = this.carpoolingService.getCarpooling(id);
        return mapper.map(carpooling, CarpoolingDTO.class);
    }

    @GetMapping("user-booking")
    public List<CarpoolingBookingDTO> getUserBooking(
            @RequestParam Boolean isArchived
    ) throws Exception {
        List<Carpooling> carpoolingList = this.carpoolingService.getUserCarpoolings(isArchived);

        ModelMapper mapper = new ModelMapper();

        return carpoolingList.stream()
                .map(carpooling -> {
                    CarpoolingBookingDTO dto = mapper.map(carpooling, CarpoolingBookingDTO.class);
                    dto.setNbPlacesRemaining(carpoolingService.getNbPlacesRemaining(carpooling));
                    if (carpooling.getVehicle() != null) {
                        dto.setVehicle(carpooling.getVehicle().getBrandModel());
                    } else {
                        dto.setVehicle("Inconnu");
                    }
                    return dto;
                })
                .toList();
    }
}
