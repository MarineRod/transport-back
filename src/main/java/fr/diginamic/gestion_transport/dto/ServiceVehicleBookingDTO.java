package fr.diginamic.gestion_transport.dto;

import java.time.LocalDateTime;

import fr.diginamic.gestion_transport.entites.ServiceVehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceVehicleBookingDTO {

	private Integer id;
	private LocalDateTime dateTimeStart;
	private LocalDateTime dateTimeEnd;
	private String licensePlateNumber;
	private UserDTO user;

}