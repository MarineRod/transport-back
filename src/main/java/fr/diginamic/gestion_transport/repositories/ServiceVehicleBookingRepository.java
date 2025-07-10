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
	
	@Query("""
		    SELECT COUNT(b) > 0 FROM ServiceVehicleBooking b
		    WHERE b.serviceVehicle.id = :vehicleId
		      AND (
		            (:start BETWEEN b.dateTimeStart AND b.dateTimeEnd)
		         OR (:end BETWEEN b.dateTimeStart AND b.dateTimeEnd)
		         OR (b.dateTimeStart BETWEEN :start AND :end)
		         OR (b.dateTimeEnd BETWEEN :start AND :end)
		      )
		      AND (:bookingId IS NULL OR b.id <> :bookingId)
		""")
		boolean existsOverlappingBooking(
		    @Param("vehicleId") String vehicleId,
		    @Param("start") LocalDateTime start,
		    @Param("end") LocalDateTime end,
		    @Param("bookingId") Integer bookingId
		);

}