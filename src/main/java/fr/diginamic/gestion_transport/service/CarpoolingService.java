package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.dto.CarpoolingDTO;
import fr.diginamic.gestion_transport.entites.Carpooling;
import fr.diginamic.gestion_transport.repositories.CarpoolingRepository;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import io.micrometer.common.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class CarpoolingService {

    private final ModelMapper mapper = ModelMapperCfg.getInstance();

    private final Logger LOG = LoggerFactory.getLogger(CarpoolingService.class);

    private final CarpoolingRepository carpoolingRepository;

    public CarpoolingService(CarpoolingRepository carpoolingRepository) {
        this.carpoolingRepository = carpoolingRepository;
    }

    public Carpooling saveCarpooling(CarpoolingDTO carpooling) throws Exception {
        try {
            if (isInError(carpooling)) throw new Exception("Impossible d'enregistrer l'annonce de covoiturage");
            if (carpooling.getId() != 0) {
                Carpooling oldCarpooling = this.carpoolingRepository.findById(carpooling.getId()).orElse(null);
                if (oldCarpooling != null && !CollectionUtils.isEmpty(oldCarpooling.getUsers())) {
                    throw new Exception("Impossible de modifier l'annonce de covoiturage");
                }
            }
            Carpooling entity = mapper.map(carpooling, Carpooling.class);
            return this.carpoolingRepository.save(entity);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible d'enregistrer l'annonce de covoiturage");
        }
    }

    private boolean isInError(CarpoolingDTO carpooling) {
        if (carpooling.getVehicle() == null) return true;
        if (carpooling.getVehicle().getNbSeats() == null || carpooling.getVehicle().getNbSeats() < 1) return true;
        if (carpooling.getDuration() == null || carpooling.getDuration() < 1) return true;
        if (carpooling.getDistance() == null || carpooling.getDistance() < 1) return true;
        if (carpooling.getDateTimeStart() == null) return true;
        if (StringUtils.isBlank(carpooling.getDepartureAddress())) return true;
        if (StringUtils.isBlank(carpooling.getArrivalAddress())) return true;
        return false;
    }

    public void deleteCarpooling(Integer id, Long userId) throws Exception {
        try {
            int result = this.carpoolingRepository.deleteByIdAndUserId(id, userId);
            if (result == 0)
                throw new Exception("Impossible de supprimer l'annonce de covoiturage");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de supprimer l'annonce de covoiturage");
        }
    }

    public Carpooling getCarpooling(Integer id) throws Exception {
        try {
            Carpooling carpooling = this.carpoolingRepository.findById(id).orElse(null);
            if (carpooling == null)
                throw new Exception("Impossible de trouver l'annonce de covoiturage");
            return carpooling;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de trouver l'annonce de covoiturage");
        }
    }
}
