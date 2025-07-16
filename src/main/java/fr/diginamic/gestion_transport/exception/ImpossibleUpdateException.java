package fr.diginamic.gestion_transport.exception;

public class ImpossibleUpdateException extends Exception {

    public ImpossibleUpdateException(String message) {
        super(message);
    }

    public ImpossibleUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
