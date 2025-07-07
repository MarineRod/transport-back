package fr.diginamic.gestion_transport.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.diginamic.gestion_transport.entites.ServiceVehicleBooking;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface ServiceVehicleBookingRepository extends JpaRepository<ServiceVehicleBooking, Integer> {
	List<ServiceVehicleBooking> findByServiceVehicle_LicensePlateNumber(String licensePlateNumber);
	
	@Query("""
		    SELECT b FROM ServiceVehicleBooking b 
		    WHERE b.user.id = :id 
		      AND (
		        b.dateTimeStart > :now 
		        OR (:now BETWEEN b.dateTimeStart AND b.dateTimeEnd)
		      )
		""")
		List<ServiceVehicleBooking> findCurrentAndUpcomingBookings(
		    @Param("id") Long id,
		    @Param("now") LocalDateTime now
		);

	@Query("""
			    SELECT b FROM ServiceVehicleBooking b
			    WHERE b.user.id = :id
			      AND b.dateTimeStart < :now
			      AND b.dateTimeEnd < :now
			""")
	List<ServiceVehicleBooking> findPastBookingsFullyEnded(@Param("id") Long id, @Param("now") LocalDateTime now);

	@Modifying
	@Query("DELETE FROM ServiceVehicleBooking b WHERE b.id = :id AND b.user.id = :userId")
	int deleteByIdAndUserId(@Param("id") Integer id, @Param("userId") Long userId);
}