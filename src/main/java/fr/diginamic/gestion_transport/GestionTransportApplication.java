package fr.diginamic.gestion_transport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Démarre l'application Spring Boot
 */
@SpringBootApplication

public class GestionTransportApplication {

	/** Méthode exécutable
	 * @param args non utilisé ici
	 */
	public static void main(String[] args) {
		SpringApplication.run(GestionTransportApplication.class, args);
	
	}
	
    
}
