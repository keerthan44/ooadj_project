package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Motorbike;
import com.mycompany.myapp.domain.enumeration.BikeStatus;
import com.mycompany.myapp.factory.MotorbikeFactory;
import com.mycompany.myapp.repository.MotorbikeRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Motorbike}.
 */
@Service
@Transactional
public class MotorbikeService {

    private final Logger log = LoggerFactory.getLogger(MotorbikeService.class);

    private final MotorbikeRepository motorbikeRepository;

    public MotorbikeService(MotorbikeRepository motorbikeRepository) {
        this.motorbikeRepository = motorbikeRepository;
    }

    public Motorbike createAndSaveMotorbike(String make, String model) {
        Motorbike motorbike = MotorbikeFactory.createMotorbike(make, model);
        return motorbikeRepository.save(motorbike); // Save the newly created motorbike
    }

    public Motorbike updateMotorbikeStatus(Long id, BikeStatus newStatus) {
        Motorbike motorbike = motorbikeRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Motorbike not found"));

        motorbike.setStatus(newStatus); // Update the status
        return motorbikeRepository.save(motorbike); // Save the updated motorbike
    }

    /**
     * Save a motorbike.
     *
     * @param motorbike the entity to save.
     * @return the persisted entity.
     */
    public Motorbike save(Motorbike motorbike) {
        log.debug("Request to save Motorbike : {}", motorbike);
        return motorbikeRepository.save(motorbike);
    }

    /**
     * Update a motorbike.
     *
     * @param motorbike the entity to save.
     * @return the persisted entity.
     */
    public Motorbike update(Motorbike motorbike) {
        log.debug("Request to update Motorbike : {}", motorbike);
        return motorbikeRepository.save(motorbike);
    }

    /**
     * Partially update a motorbike.
     *
     * @param motorbike the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Motorbike> partialUpdate(Motorbike motorbike) {
        log.debug("Request to partially update Motorbike : {}", motorbike);

        return motorbikeRepository
            .findById(motorbike.getId())
            .map(existingMotorbike -> {
                if (motorbike.getMake() != null) {
                    existingMotorbike.setMake(motorbike.getMake());
                }
                if (motorbike.getModel() != null) {
                    existingMotorbike.setModel(motorbike.getModel());
                }
                if (motorbike.getStatus() != null) {
                    existingMotorbike.setStatus(motorbike.getStatus());
                }

                return existingMotorbike;
            })
            .map(motorbikeRepository::save);
    }

    /**
     * Get all the motorbikes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Motorbike> findAll() {
        log.debug("Request to get all Motorbikes");
        return motorbikeRepository.findAll();
    }

    /**
     * Get one motorbike by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Motorbike> findOne(Long id) {
        log.debug("Request to get Motorbike : {}", id);
        return motorbikeRepository.findById(id);
    }

    /**
     * Delete the motorbike by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Motorbike : {}", id);
        motorbikeRepository.deleteById(id);
    }
}
