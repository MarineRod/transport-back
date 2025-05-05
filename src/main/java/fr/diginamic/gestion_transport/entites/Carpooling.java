package fr.diginamic.gestion_transport.entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
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

	public Carpooling() {
		
	}

	public Carpooling(Integer id, Double distance, LocalDateTime dateTimeStart, String departureAddress,
			String arrivalAddress, Integer duration, Vehicle vehicle, User organisator, Set<User> users) {
		super();
		this.id = id;
		this.distance = distance;
		this.dateTimeStart = dateTimeStart;
		this.departureAddress = departureAddress;
		this.arrivalAddress = arrivalAddress;
		this.duration = duration;
		this.vehicle = vehicle;
		this.organisator = organisator;
		this.users = users;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public LocalDateTime getDateTimeStart() {
		return dateTimeStart;
	}

	public void setDateTimeStart(LocalDateTime dateTimeStart) {
		this.dateTimeStart = dateTimeStart;
	}

	public String getDepartureAddress() {
		return departureAddress;
	}

	public void setDepartureAddress(String departureAddress) {
		this.departureAddress = departureAddress;
	}

	public String getArrivalAddress() {
		return arrivalAddress;
	}

	public void setArrivalAddress(String arrivalAddress) {
		this.arrivalAddress = arrivalAddress;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public User getOrganisator() {
		return organisator;
	}

	public void setOrganisator(User organisator) {
		this.organisator = organisator;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	
}
