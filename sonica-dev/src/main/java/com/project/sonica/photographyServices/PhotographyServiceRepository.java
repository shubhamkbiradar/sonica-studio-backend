package com.project.sonica.photographyServices;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.sonica.security.User;

public interface PhotographyServiceRepository extends JpaRepository<PhotographyServices, Long> {
	
	List<PhotographyServices> findByPhotographer(User photographer);

	PhotographyServices save(PhotographyService service);
}
