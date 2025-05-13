package fr.diginamic.gestion_transport.service;

import java.util.List;
import java.util.stream.Collectors;

import fr.diginamic.gestion_transport.tools.ModelMapperCfg;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.diginamic.gestion_transport.dto.ServiceVehicleBookingDTO;
import fr.diginamic.gestion_transport.entites.ServiceVehicleBooking;
import fr.diginamic.gestion_transport.repositories.ServiceVehicleBookingRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SerVehicleBookingService {
	private final ModelMapper modelMapper= ModelMapperCfg.getInstance();
	private final ServiceVehicleBookingRepository serviceVehicleBookingRepository;

	public SerVehicleBookingService(
			ServiceVehicleBookingRepository serviceVehicleBookingRepository) {
		this.serviceVehicleBookingRepository = serviceVehicleBookingRepository;
	}
	
	private ServiceVehicleBookingDTO convertToDto(ServiceVehicleBooking booking) {
	    modelMapper.typeMap(ServiceVehicleBooking.class, ServiceVehicleBookingDTO.class).addMappings(mapper -> {
	        mapper.map(src -> src.getServiceVehicle().getLicensePlateNumber(), ServiceVehicleBookingDTO::setLicensePlateNumber);
	    });
	    return modelMapper.map(booking, ServiceVehicleBookingDTO.class);
	}

	private ServiceVehicleBooking convertToEntity(ServiceVehicleBookingDTO bookingDto) {
		return modelMapper.map(bookingDto, ServiceVehicleBooking.class);
	}

	public ServiceVehicleBookingDTO createBooking(ServiceVehicleBookingDTO bookingDto) {
		ServiceVehicleBooking booking = convertToEntity(bookingDto);
		ServiceVehicleBooking savedBooking = serviceVehicleBookingRepository.save(booking);
		return convertToDto(savedBooking);
	}

	public List<ServiceVehicleBookingDTO> getAllBookings() {
	    return serviceVehicleBookingRepository.findAll().stream()
	            .map(this::convertToDto)
	            .collect(Collectors.toList());
	}
	
	public List<ServiceVehicleBookingDTO> getBookingsByServiceVehiculeId(String serviceVehiculeId) {
		return serviceVehicleBookingRepository.findByServiceVehicle_LicensePlateNumber(serviceVehiculeId).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}
	
	public ServiceVehicleBookingDTO getBookingByBookingId(Integer bookingId) {
	    ServiceVehicleBooking booking = serviceVehicleBookingRepository.findById(bookingId)
	            .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'ID : " + bookingId));
	    return convertToDto(booking);
	}

	public ServiceVehicleBookingDTO updateBooking(Integer id, ServiceVehicleBookingDTO dto) {
	    ServiceVehicleBooking booking = serviceVehicleBookingRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'ID : " + id));

	    booking.setDateTimeStart(dto.getDateTimeStart());
	    booking.setDateTimeEnd(dto.getDateTimeEnd());

	    ServiceVehicleBooking updated = serviceVehicleBookingRepository.save(booking);
	    return convertToDto(updated);
	}

	public void deleteBooking(Integer id) {
		ServiceVehicleBooking booking = serviceVehicleBookingRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'ID : " + id));
		serviceVehicleBookingRepository.delete(booking);
	}

}