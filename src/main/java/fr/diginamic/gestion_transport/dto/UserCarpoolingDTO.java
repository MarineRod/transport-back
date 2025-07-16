package fr.diginamic.gestion_transport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCarpoolingDTO {
    private Long idUser;
    private Long idCarpooling;
}