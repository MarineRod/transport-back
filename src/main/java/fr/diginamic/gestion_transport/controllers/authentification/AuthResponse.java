package fr.diginamic.gestion_transport.controllers.authentification;

import fr.diginamic.gestion_transport.entites.User;

import java.util.Optional;

/**
 * Sert à retourner le token JWT dans le body de la réponse
 */
public class AuthResponse {
	
    /** token jwt */
    private String jwt;

	private String firstName;

	private String lastName;
    
    /** Constructeur
     * @param jwt valeur du token JWT
     */
	public AuthResponse(String jwt, User user) {
		this.jwt = jwt;
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
	}

	/** Getter
	 * @return the jwt
	 */
	public String getJwt() {
		return jwt;
	}


	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}