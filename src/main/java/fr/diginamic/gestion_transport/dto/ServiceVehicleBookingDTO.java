package fr.diginamic.gestion_transport.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceVehicleBookingDTO {

	private Integer id;
	private LocalDateTime dateTimeStart;
	private LocalDateTime dateTimeEnd;
	private String licensePlateNumber;
	private UserDTO user;

}