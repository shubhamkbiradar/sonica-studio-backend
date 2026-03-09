package com.project.sonica.photographyServices;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer/services")
public class CustomerServiceController {

    private final PhotographyService manager;

    public CustomerServiceController(PhotographyService manager) {
        this.manager = manager;
    }

    @GetMapping("/{photographerId}")
    public ResponseEntity<List<PhotographyServices>> getServices(@PathVariable Long photographerId) {
        return ResponseEntity.ok(manager.getServicesByPhotographer(photographerId));
    }
}
