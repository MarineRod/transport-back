package fr.diginamic.gestion_transport.entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
@Entity
public class ServiceVehicleBooking {

	@Id
	private Integer id;
	@Column(nullable = false)
	private LocalDateTime dateTimeStart;
	@Column(nullable = false)
	private LocalDateTime dateTimeEnd;

	@ManyToOne
	@JoinColumn(name = "SERVICE_VEHICLE_ID")
	private ServiceVehicle serviceVehicle;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private User user;

	public ServiceVehicleBooking() {

	}

	public ServiceVehicleBooking(Integer id, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd,
			ServiceVehicle serviceVehicle, User user) {
		super();
		this.id = id;
		this.dateTimeStart = dateTimeStart;
		this.dateTimeEnd = dateTimeEnd;
		this.serviceVehicle = serviceVehicle;
		this.user = user;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getDateTimeStart() {
		return dateTimeStart;
	}

	public void setDateTimeStart(LocalDateTime dateTimeStart) {
		this.dateTimeStart = dateTimeStart;
	}

	public LocalDateTime getDateTimeEnd() {
		return dateTimeEnd;
	}

	public void setDateTimeEnd(LocalDateTime dateTimeEnd) {
		this.dateTimeEnd = dateTimeEnd;
	}

	public ServiceVehicle getServiceVehicle() {
		return serviceVehicle;
	}

	public void setServiceVehicle(ServiceVehicle serviceVehicle) {
		this.serviceVehicle = serviceVehicle;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
