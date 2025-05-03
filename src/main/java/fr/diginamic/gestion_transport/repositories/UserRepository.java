package fr.diginamic.gestion_transport.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.diginamic.gestion_transport.entites.User;

/**
 * Accès base de données
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
	/** Recherche un User via son username
     * @param username username
     * @return {@link Optional}
     */
    Optional<User> findByUsername(String username);
}