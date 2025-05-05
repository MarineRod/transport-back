package fr.diginamic.gestion_transport.entites;


import fr.diginamic.gestion_transport.enums.CategoryEnum;
import fr.diginamic.gestion_transport.enums.MotorizationEnum;
import fr.diginamic.gestion_transport.enums.VehicleStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceVehicle extends Vehicle{
	private Integer co2Km;
	@Column(nullable = false)
	private String photoUrl;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private VehicleStatusEnum status;
	@Enumerated(EnumType.STRING)
	private CategoryEnum category;
	@Enumerated(EnumType.STRING)
	private MotorizationEnum motorization;
	@OneToMany(mappedBy="serviceVehicle")
	private List<ServiceVehicleBooking> serviceVehicleBookings;
}
