package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Maintenance;
import com.mycompany.myapp.repository.MaintenanceRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Maintenance}.
 */
@Service
@Transactional
public class MaintenanceService {

    private final Logger log = LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    /**
     * Save a maintenance.
     *
     * @param maintenance the entity to save.
     * @return the persisted entity.
     */
    public Maintenance save(Maintenance maintenance) {
        log.debug("Request to save Maintenance : {}", maintenance);
        return maintenanceRepository.save(maintenance);
    }

    /**
     * Update a maintenance.
     *
     * @param maintenance the entity to save.
     * @return the persisted entity.
     */
    public Maintenance update(Maintenance maintenance) {
        log.debug("Request to update Maintenance : {}", maintenance);
        return maintenanceRepository.save(maintenance);
    }

    /**
     * Partially update a maintenance.
     *
     * @param maintenance the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Maintenance> partialUpdate(Maintenance maintenance) {
        log.debug("Request to partially update Maintenance : {}", maintenance);

        return maintenanceRepository
            .findById(maintenance.getId())
            .map(existingMaintenance -> {
                if (maintenance.getDescription() != null) {
                    existingMaintenance.setDescription(maintenance.getDescription());
                }
                if (maintenance.getStartDate() != null) {
                    existingMaintenance.setStartDate(maintenance.getStartDate());
                }
                if (maintenance.getEndDate() != null) {
                    existingMaintenance.setEndDate(maintenance.getEndDate());
                }

                return existingMaintenance;
            })
            .map(maintenanceRepository::save);
    }

    /**
     * Get all the maintenances.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Maintenance> findAll() {
        log.debug("Request to get all Maintenances");
        return maintenanceRepository.findAll();
    }

    /**
     * Get one maintenance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Maintenance> findOne(Long id) {
        log.debug("Request to get Maintenance : {}", id);
        return maintenanceRepository.findById(id);
    }

    /**
     * Delete the maintenance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Maintenance : {}", id);
        maintenanceRepository.deleteById(id);
    }
}
