package com.project.sonica.photographyServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.sonica.security.User;
import com.project.sonica.security.UserRepository;

@Service
public class PhotographyService {

	private final PhotographyServiceRepository photohraphyServicesRepository;
	private final UserRepository userRepository;

	public PhotographyService(PhotographyServiceRepository repository, UserRepository userRepository) {
		this.photohraphyServicesRepository = repository;
		this.userRepository = userRepository;
	}

	// Photographer adds a new service
	public PhotographyServices addService(Long photographerId, PhotographyServices service) {
		User photographer = userRepository.findById(photographerId)
				.orElseThrow(() -> new RuntimeException("Photographer not found"));
		service.setPhotographer(photographer);
		return photohraphyServicesRepository.save(service);
	}

	// Photographer updates a service
	public PhotographyServices updateService(Long serviceId, PhotographyServices updatedService) {
		PhotographyServices existing = photohraphyServicesRepository.findById(serviceId)
				.orElseThrow(() -> new RuntimeException("Service not found"));
		existing.setServiceName(updatedService.getServiceName());
		existing.setDescription(updatedService.getDescription());
		existing.setPrice(updatedService.getPrice());
		existing.setDiscount(updatedService.getDiscount());
		return photohraphyServicesRepository.save(existing);
	}

	// Photographer deletes a service
	public void deleteService(Long serviceId) {
		photohraphyServicesRepository.deleteById(serviceId);
	}

	// Customer views services of a photographer
	public List<PhotographyServices> getServicesByPhotographer(Long photographerId) {
		User photographer = userRepository.findById(photographerId)
				.orElseThrow(() -> new RuntimeException("Photographer not found"));
		return photohraphyServicesRepository.findByPhotographer(photographer);
	}
}
