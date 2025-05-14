package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.dto.CarpoolingDTO;
import fr.diginamic.gestion_transport.dto.MailSendingSettingsDTO;
import fr.diginamic.gestion_transport.entites.Carpooling;
import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.enums.MailTemplateEnum;
import fr.diginamic.gestion_transport.repositories.CarpoolingRepository;
import fr.diginamic.gestion_transport.repositories.UserRepository;
import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class CarpoolingService {

    private final Logger LOG = LoggerFactory.getLogger(CarpoolingService.class);
    private final ModelMapper mapper = ModelMapperCfg.getInstance();

    private final CarpoolingRepository carpoolingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public CarpoolingService(CarpoolingRepository carpoolingRepository, UserService userService, UserRepository userRepository, EmailService emailService) {
        this.carpoolingRepository = carpoolingRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional(rollbackOn = Exception.class)
    public Carpooling saveCarpooling(CarpoolingDTO carpooling) throws Exception {
        try {
            List<String> errors = isInError(carpooling);
            if (!CollectionUtils.isEmpty(errors)) throw new Exception(String.join(", ", errors));
            if (carpooling.getId() != null) {
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

    private List<String> isInError(CarpoolingDTO carpooling) {
        List<String> errors = new ArrayList<>();
        if (carpooling.getVehicle() == null) errors.add("Véhicule non renseigné");
        if (carpooling.getVehicle().getNbSeats() == null || carpooling.getVehicle().getNbSeats() < 1) errors.add("Le Nombre de place est invalide");
        if (carpooling.getDuration() == null || carpooling.getDuration() < 1) errors.add("La durée est invalide");
        if (carpooling.getDistance() == null || carpooling.getDistance() < 1) errors.add("La distance est invalide");
        if (carpooling.getDateTimeStart() == null) errors.add("La date de départ est invalide");
        if (StringUtils.isBlank(carpooling.getDepartureAddress())) errors.add("L'adresse de départ est invalide");
        if (StringUtils.isBlank(carpooling.getArrivalAddress())) errors.add("L'adresse d'arrivée est invalide");
        return errors;
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteCarpooling(Integer id, Long userId) throws Exception {
        try {
            Carpooling carpooling = this.carpoolingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ce covoiturage n'existe pas"));
            int result = this.carpoolingRepository.deleteByIdAndUserId(id, userId);
            if (result == 0)
                throw new Exception("Impossible de supprimer l'annonce de covoiturage");
            Map<String, String> context = prepareContext(carpooling);

            for (User user : carpooling.getUsers()) {
                MailSendingSettingsDTO settings = new MailSendingSettingsDTO().builder()
                        .to(user.getUsername())
                        .subject("Covoiturage annulé")
                        .mailTemplateEnum(MailTemplateEnum.CANCELED_CARPOOLING)
                        .contextData(context)
                        .build();
                this.emailService.sendHtmlMail(settings);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de supprimer l'annonce de covoiturage");
        }
    }

    private Map<String, String> prepareContext(Carpooling carpooling) {
        Map<String, String> context = new HashMap<>();
        context.put("dateTimeStart", carpooling.getDateTimeStart().toString());
        context.put("departureAddress", carpooling.getDepartureAddress());
        return context;
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

    public Set<User> getCarpoolingParticipantList(Integer id) throws Exception {
        try {
            Carpooling carpooling = this.carpoolingRepository.findById(id).orElse(null);
            if (carpooling == null)
                throw new Exception("Impossible de trouver ce covoiturage");
            return carpooling.getUsers();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de trouver ce covoiturage");
        }
    }

    public List<Carpooling> getUserBookings(Boolean isArchived) throws Exception {

        User connectedUser = userService.getConnectedUser();
        try {
            if (!isArchived) {
                return this.carpoolingRepository.findCarpoolingByUsersContainsAndDateTimeStartAfter(connectedUser, LocalDateTime.now());
            } else {
                return this.carpoolingRepository.findCarpoolingByUsersContainsAndDateTimeStartBefore(connectedUser, LocalDateTime.now());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de récupérer la liste des réservations de covoiturage");
        }
    }

    public Integer getNbPlacesRemaining(Carpooling carpooling) {
        Set<User> participants = carpooling.getUsers();
        int nbPlaces = carpooling.getNbSeats();
        return nbPlaces - participants.size();
    }

    @Transactional
    public void cancelUserBooking(Integer idCarpooling) {
        User user = userService.getConnectedUser();
        Carpooling carpooling = this.carpoolingRepository.findById(idCarpooling).orElseThrow(() -> new EntityNotFoundException("Ce covoiturage n'existe pas"));

        carpooling.getUsers().remove(user);
        user.getCarpoolings().remove(carpooling);


        this.carpoolingRepository.save(carpooling);
        this.userRepository.save(user);
    }

    @Transactional
    public void saveUserBooking(Integer idCarpooling) {
        User user = userService.getConnectedUser();
        Carpooling carpooling = this.carpoolingRepository.findById(idCarpooling).orElseThrow(() -> new EntityNotFoundException("Ce covoiturage n'existe pas"));

        carpooling.getUsers().add(user);
        user.getCarpoolings().add(carpooling);


        this.carpoolingRepository.save(carpooling);
        this.userRepository.save(user);
    }

    public List<Carpooling> getAllOrganisatorCarpooling(boolean isArchived) throws Exception {
        try {
            User user = userService.getConnectedUser();
            if (isArchived)
                return this.carpoolingRepository.findAllByOrganisatorIdAndDateTimeStartBefore(user.getId(), LocalDateTime.now());
            else
                return this.carpoolingRepository.findAllByOrganisatorIdAndDateTimeStartAfter(user.getId(), LocalDateTime.now());
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception("Impossible de récupérer la liste de mes covoiturages");
        }
    }

    public List<Carpooling> search(String departureAddress, String arrivalAddress, LocalDate dateTimeStart) throws Exception {
        int nbCriteria = 0;
        if (departureAddress != null) nbCriteria++;
        if (arrivalAddress != null) nbCriteria++;
        if (dateTimeStart != null) nbCriteria++;
        if (nbCriteria < 1){
            throw new Exception("Veuillez saisir au moins un critère de recherche");
        }
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (dateTimeStart != null){
            start = LocalDateTime.of(dateTimeStart, LocalTime.MIN);
            end = LocalDateTime.of(dateTimeStart, LocalTime.MAX);
        }
        return this.carpoolingRepository.findAllByDepartureAddressContainingIgnoreCaseOrArrivalAddressContainingIgnoreCaseOrDateTimeStartBetween(departureAddress, arrivalAddress, start, end);
    }

    public Boolean hasUserBooked(Integer idCarpooling){
        User user = userService.getConnectedUser();
        Carpooling carpooling = this.carpoolingRepository.findById(idCarpooling).orElseThrow(() -> new EntityNotFoundException("Ce covoiturage n'existe pas"));

        return carpooling.getUsers().contains(user);
    }
}
