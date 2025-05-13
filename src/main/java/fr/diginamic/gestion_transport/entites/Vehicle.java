package fr.diginamic.gestion_transport.entites;

import fr.diginamic.gestion_transport.enums.BrandEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private User user;

    public String getBrandModel(){
        return Brand.name().toUpperCase() + " " + model;
    }
}
