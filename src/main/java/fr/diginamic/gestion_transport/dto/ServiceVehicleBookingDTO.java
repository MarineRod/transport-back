package fr.diginamic.gestion_transport.dto;

import java.time.LocalDateTime;

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
public class ServiceVehicleBookingDTO {

	private LocalDateTime dateTimeStart;
	private LocalDateTime dateTimeEnd;
	private String licensePlateNumber;

}