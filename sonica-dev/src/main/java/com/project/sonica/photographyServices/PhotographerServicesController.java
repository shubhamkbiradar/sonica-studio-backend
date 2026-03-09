package com.project.sonica.photographyServices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/photographer/services")
public class PhotographerServicesController {

    private final PhotographyService manager;

    public PhotographerServicesController(PhotographyService manager) {
        this.manager = manager;
    }

    @PostMapping("/{photographerId}")
    public ResponseEntity<PhotographyServices> addService(
            @PathVariable Long photographerId,
            @RequestBody PhotographyServices service) {
        return ResponseEntity.ok(manager.addService(photographerId, service));
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<PhotographyServices> updateService(
            @PathVariable Long serviceId,
            @RequestBody PhotographyServices service) {
        return ResponseEntity.ok(manager.updateService(serviceId, service));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteService(@PathVariable Long serviceId) {
        manager.deleteService(serviceId);
        return ResponseEntity.noContent().build();
    }
}
