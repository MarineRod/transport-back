package fr.diginamic.gestion_transport.exception;

public class VehicleGetListException extends Exception {

    public VehicleGetListException(String message) {
        super(message);
    }

    public VehicleGetListException(String message, Throwable cause) {
        super(message, cause);
    }
}
