package fr.diginamic.gestion_transport.exception;

public class InvalidBookingDatesException extends RuntimeException {
    public InvalidBookingDatesException(String message) {
        super(message);
    }
}
