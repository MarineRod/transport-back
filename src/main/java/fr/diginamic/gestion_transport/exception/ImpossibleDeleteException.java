package fr.diginamic.gestion_transport.exception;

public class ImpossibleDeleteException extends Exception {

    public ImpossibleDeleteException(String message) {
        super(message);
    }

    public ImpossibleDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
