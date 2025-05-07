package fr.diginamic.gestion_transport.dto;

import fr.diginamic.gestion_transport.enums.BrandEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private String licensePlateNumber;
    private String model;
    private Integer nbSeats;
    private BrandEnum brand;
    private UserDTO user;
}
