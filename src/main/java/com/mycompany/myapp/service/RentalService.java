package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Rental;
import com.mycompany.myapp.repository.RentalRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Rental}.
 */
@Service
@Transactional
public class RentalService {

    private final Logger log = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    /**
     * Save a rental.
     *
     * @param rental the entity to save.
     * @return the persisted entity.
     */
    public Rental save(Rental rental) {
        log.debug("Request to save Rental : {}", rental);
        return rentalRepository.save(rental);
    }

    /**
     * Update a rental.
     *
     * @param rental the entity to save.
     * @return the persisted entity.
     */
    public Rental update(Rental rental) {
        log.debug("Request to update Rental : {}", rental);
        return rentalRepository.save(rental);
    }

    /**
     * Partially update a rental.
     *
     * @param rental the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Rental> partialUpdate(Rental rental) {
        log.debug("Request to partially update Rental : {}", rental);

        return rentalRepository
            .findById(rental.getId())
            .map(existingRental -> {
                if (rental.getStartDate() != null) {
                    existingRental.setStartDate(rental.getStartDate());
                }
                if (rental.getDueDate() != null) {
                    existingRental.setDueDate(rental.getDueDate());
                }
                if (rental.getReturnDate() != null) {
                    existingRental.setReturnDate(rental.getReturnDate());
                }

                return existingRental;
            })
            .map(rentalRepository::save);
    }

    /**
     * Get all the rentals.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Rental> findAll() {
        log.debug("Request to get all Rentals");
        return rentalRepository.findAll();
    }

    /**
     * Get one rental by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Rental> findOne(Long id) {
        log.debug("Request to get Rental : {}", id);
        return rentalRepository.findById(id);
    }

    /**
     * Delete the rental by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Rental : {}", id);
        rentalRepository.deleteById(id);
    }
}
