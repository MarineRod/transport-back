package fr.diginamic.gestion_transport.exception;



public class BookingConflictException extends RuntimeException {
	
	public BookingConflictException(String message) {
		super(message);
	}
}
