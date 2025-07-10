package fr.diginamic.gestion_transport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarpoolingBookingDTO {
    private Long id;
    private Double distance;
    private LocalDateTime dateTimeStart;
    private String departureAddress;
    private String arrivalAddress;
    private Integer duration;
    private Integer nbPlacesRemaining;
    private String vehicle;
}