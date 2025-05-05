package fr.diginamic.gestion_transport.entites;


import fr.diginamic.gestion_transport.enums.CategoryEnum;
import fr.diginamic.gestion_transport.enums.VehicleStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
	private VehicleStatusEnum vehicle;
	@Column(nullable = false)
	private CategoryEnum category;
	
	@OneToMany(mappedBy="serviceVehicle")
	private List<ServiceVehicleBooking> serviceVehicleBookings;

	public ServiceVehicle() {
	}

	public ServiceVehicle(Integer co2Km, String photoUrl, VehicleStatusEnum vehicle, CategoryEnum category,
			List<ServiceVehicleBooking> serviceVehicleBookings) {
		super();
		this.co2Km = co2Km;
		this.photoUrl = photoUrl;
		this.vehicle = vehicle;
		this.category = category;
		this.serviceVehicleBookings = serviceVehicleBookings;
	}

	public Integer getCo2Km() {
		return co2Km;
	}

	public void setCo2Km(Integer co2Km) {
		this.co2Km = co2Km;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public VehicleStatusEnum getVehicle() {
		return vehicle;
	}

	public void setVehicle(VehicleStatusEnum vehicle) {
		this.vehicle = vehicle;
	}

	public CategoryEnum getCategory() {
		return category;
	}

	public void setCategory(CategoryEnum category) {
		this.category = category;
	}

	public List<ServiceVehicleBooking> getServiceVehicleBookings() {
		return serviceVehicleBookings;
	}

	public void setServiceVehicleBookings(List<ServiceVehicleBooking> serviceVehicleBookings) {
		this.serviceVehicleBookings = serviceVehicleBookings;
	}

	
}
