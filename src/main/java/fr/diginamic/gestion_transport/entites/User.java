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
}