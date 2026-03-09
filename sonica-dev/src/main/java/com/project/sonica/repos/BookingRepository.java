package com.project.sonica.repos;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Customer;
import com.project.sonica.photographyServices.PhotographyServices;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
	List<Booking> findByCustomer(Customer customer);

	List<Booking> findByEventDate(LocalDate eventDate);

	List<Booking> findByStatus(String status);

	Page<Booking> findAll(Specification<Booking> spec, Pageable pageable);

	List<Booking> findByPhotographerName(String photographerName);

	List<Booking> findByCustomerName(String customerName);
	
	List<Booking> findByService(PhotographyServices photographyServices);

}
