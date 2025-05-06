package fr.diginamic.gestion_transport.repositories;

import fr.diginamic.gestion_transport.entites.ServiceVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ServiceVehicleRepository extends JpaRepository<ServiceVehicle, Integer> {

    ServiceVehicle findByLicensePlateNumber(String licensePlateNumber);

    void deleteByLicensePlateNumber(String licensePlateNumber);
}
