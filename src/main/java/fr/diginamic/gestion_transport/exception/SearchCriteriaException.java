package fr.diginamic.gestion_transport.exception;

public class SearchCriteriaException extends Exception {

    public SearchCriteriaException(String message) {
        super(message);
    }

    public SearchCriteriaException(String message, Throwable cause) {
        super(message, cause);
    }
}
