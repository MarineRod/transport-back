package fr.diginamic.gestion_transport.entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Carpooling {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private Double distance;
	@Column(nullable = false)
	private LocalDateTime dateTimeStart;
	@Column(nullable = false)
	private String departureAddress;
	@Column(nullable = false)
	private String arrivalAddress;
	@Column(nullable = false)
	private Integer duration;


	@ManyToOne
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User organisator;

	@ManyToMany
	@JoinTable(name = "USER_CARPOOLING",
			joinColumns = @JoinColumn(name = "ID_CARPOOLING", referencedColumnName = "ID"),
			inverseJoinColumns = @JoinColumn(name = "ID_USER", referencedColumnName = "ID")
	)
	private Set<User> users;
}
