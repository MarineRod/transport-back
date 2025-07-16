package fr.diginamic.gestion_transport.dto;

import fr.diginamic.gestion_transport.entites.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantListDTO {
    private Set<User> participants;
}