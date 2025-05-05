package fr.diginamic.gestion_transport.controllers;

import lombok.Getter;

/**
 * Sert à retourner le token JWT dans le body de la réponse
 */
class AuthResponse {

    /**
     * token jwt
     * -- GETTER --
     * Getter
     *
     * @return the jwt
     */
    @Getter
    private String jwt;

    /**
     * Constructeur
     *
     * @param jwt valeur du token JWT
     */
    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }

}