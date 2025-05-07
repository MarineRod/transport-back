package fr.diginamic.gestion_transport.dto;

import fr.diginamic.gestion_transport.entites.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarpoolingDTO {
    private Integer id;
    private Double distance;
    private LocalDateTime dateTimeStart;
    private String departureAddress;
    private String arrivalAddress;
    private Integer duration;
    private VehicleDTO vehicle;
    private UserDTO organisator;
    private Integer nbSeats;
    private List<User> users;
}
