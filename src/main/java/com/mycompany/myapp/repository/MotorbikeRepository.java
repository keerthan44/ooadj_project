package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Motorbike;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Motorbike entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MotorbikeRepository extends JpaRepository<Motorbike, Long> {}
