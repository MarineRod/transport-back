package fr.diginamic.gestion_transport.entites;

import fr.diginamic.gestion_transport.enums.BrandEnum;
import fr.diginamic.gestion_transport.enums.MotorizationEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Vehicle {
	@Id
	private String licensePlateNumber;
	@Column(nullable = false)
	private String model;
	@Column(nullable = false)
	private Integer nbSeats;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BrandEnum Brand;
	@OneToMany(mappedBy = "vehicle")
	private List<Carpooling> carpoolings;
}
