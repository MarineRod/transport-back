package fr.diginamic.gestion_transport.exception;

public class VehicleSaveException extends Exception {

    public VehicleSaveException(String message) {
        super(message);
    }

    public VehicleSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
