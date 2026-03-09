package com.project.sonica.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByBooking(Booking booking);
}
