package fr.diginamic.gestion_transport.repositories;

import fr.diginamic.gestion_transport.entites.Carpooling;
import fr.diginamic.gestion_transport.entites.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface CarpoolingRepository extends JpaRepository<Carpooling, Integer> {

    List<Carpooling> findCarpoolingByUsersContainsAndDateTimeStartAfter(User user, LocalDateTime dateNow);
    List<Carpooling> findCarpoolingByUsersContainsAndDateTimeStartBefore(User user, LocalDateTime dateNow);

    @Modifying
    @Query("DELETE FROM Carpooling cp WHERE cp.id = :id AND cp.organisator.id = :userId")
    int deleteByIdAndUserId(@Param("id") Integer id,@Param("userId") Long userId);

    List<Carpooling> findAllByOrganisatorIdAndDateTimeStartBefore(Long id, LocalDateTime now);

    List<Carpooling> findAllByOrganisatorIdAndDateTimeStartAfter(Long id, LocalDateTime now);
}
