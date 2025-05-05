package fr.diginamic.gestion_transport.repositories;

import fr.diginamic.gestion_transport.entites.ServiceVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceVehicleRepository extends JpaRepository<ServiceVehicle, Integer> {

}
