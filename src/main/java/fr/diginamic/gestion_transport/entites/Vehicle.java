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

	public Vehicle() {

	}

	public Vehicle(String licensePlateNumber, String model, Integer nbSeats, BrandEnum brand,
			List<Carpooling> carpoolings) {
		super();
		this.licensePlateNumber = licensePlateNumber;
		this.model = model;
		this.nbSeats = nbSeats;
		Brand = brand;
		this.carpoolings = carpoolings;
	}

	public String getLicensePlateNumber() {
		return licensePlateNumber;
	}

	public void setLicensePlateNumber(String licensePlateNumber) {
		this.licensePlateNumber = licensePlateNumber;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Integer getNbSeats() {
		return nbSeats;
	}

	public void setNbSeats(Integer nbSeats) {
		this.nbSeats = nbSeats;
	}

	public BrandEnum getBrand() {
		return Brand;
	}

	public void setBrand(BrandEnum brand) {
		Brand = brand;
	}

	public List<Carpooling> getCarpoolings() {
		return carpoolings;
	}

	public void setCarpoolings(List<Carpooling> carpoolings) {
		this.carpoolings = carpoolings;
	}

	
}
