package fr.diginamic.gestion_transport.repositories;

import fr.diginamic.gestion_transport.entites.Vehicle;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    List<Vehicle> findAllByUserId(Long userId);
}
