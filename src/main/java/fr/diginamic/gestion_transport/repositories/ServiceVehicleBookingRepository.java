package fr.diginamic.gestion_transport.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.diginamic.gestion_transport.entites.ServiceVehicleBooking;

/**
 * Accès base de données
 */
@Repository
public interface ServiceVehicleBookingRepository extends JpaRepository<ServiceVehicleBooking, Integer> {
	List<ServiceVehicleBooking> findByServiceVehicle_LicensePlateNumber(String licensePlateNumber);
}
