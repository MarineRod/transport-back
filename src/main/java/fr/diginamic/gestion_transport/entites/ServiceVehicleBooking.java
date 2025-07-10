package fr.diginamic.gestion_transport.entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceVehicleBooking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	
}