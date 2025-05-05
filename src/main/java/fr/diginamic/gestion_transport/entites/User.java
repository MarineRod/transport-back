package fr.diginamic.gestion_transport.entites;

import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {
    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** username : email par exemple */
    private String username;
    /** password */
    private String password;
    /** permet de désactiver un utilisateur par exemple */
    private boolean enabled;

    /** Liste des rôles */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;


	@OneToMany(mappedBy = "organisator")
	private List<Carpooling> organizedCarpoolings;

	@ManyToMany(mappedBy="users")
	private Set<Carpooling> carpoolings;

	@OneToMany(mappedBy="user")
	private Set<ServiceVehicleBooking> serviceVehicleBooking;

	public User() {
	
	}

	public User(Long id, String username, String password, boolean enabled, Set<Role> roles,
			List<Carpooling> organizedCarpoolings, Set<Carpooling> carpoolings,
			Set<ServiceVehicleBooking> serviceVehicleBooking) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.roles = roles;
		this.organizedCarpoolings = organizedCarpoolings;
		this.carpoolings = carpoolings;
		this.serviceVehicleBooking = serviceVehicleBooking;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public List<Carpooling> getOrganizedCarpoolings() {
		return organizedCarpoolings;
	}

	public void setOrganizedCarpoolings(List<Carpooling> organizedCarpoolings) {
		this.organizedCarpoolings = organizedCarpoolings;
	}

	public Set<Carpooling> getCarpoolings() {
		return carpoolings;
	}

	public void setCarpoolings(Set<Carpooling> carpoolings) {
		this.carpoolings = carpoolings;
	}

	public Set<ServiceVehicleBooking> getServiceVehicleBooking() {
		return serviceVehicleBooking;
	}

	public void setServiceVehicleBooking(Set<ServiceVehicleBooking> serviceVehicleBooking) {
		this.serviceVehicleBooking = serviceVehicleBooking;
	}

	
}