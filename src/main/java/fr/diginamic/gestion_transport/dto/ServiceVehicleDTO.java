package fr.diginamic.gestion_transport.dto;

import fr.diginamic.gestion_transport.enums.BrandEnum;
import fr.diginamic.gestion_transport.enums.CategoryEnum;
import fr.diginamic.gestion_transport.enums.MotorizationEnum;
import fr.diginamic.gestion_transport.enums.VehicleStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceVehicleDTO {
    private Integer co2Km;
    private String photoUrl;
    private VehicleStatusEnum vehicle;
    private CategoryEnum category;
    private MotorizationEnum motorization;
    private String licensePlateNumber;
    private String model;
    private Integer nbSeats;
    private BrandEnum brand;
    private VehicleStatusEnum status;
    private Boolean hasBookings;
}
