package fr.diginamic.gestion_transport.entites;

import java.util.List;

import fr.diginamic.gestion_transport.enums.CategoryEnum;
import fr.diginamic.gestion_transport.enums.MotorizationEnum;
import fr.diginamic.gestion_transport.enums.VehicleStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceVehicle extends Vehicle {
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

	@OneToMany(mappedBy = "serviceVehicle")
	private List<ServiceVehicleBooking> serviceVehicleBookings;

}