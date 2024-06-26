package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Rental;
import com.mycompany.myapp.repository.RentalRepository;
import com.mycompany.myapp.service.RentalService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Rental}.
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalResource {

    private final Logger log = LoggerFactory.getLogger(RentalResource.class);

    private static final String ENTITY_NAME = "rental";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RentalService rentalService;

    private final RentalRepository rentalRepository;

    public RentalResource(RentalService rentalService, RentalRepository rentalRepository) {
        this.rentalService = rentalService;
        this.rentalRepository = rentalRepository;
    }

    /**
     * {@code POST  /rentals} : Create a new rental.
     *
     * @param rental the rental to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rental, or with status {@code 400 (Bad Request)} if the rental has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Rental> createRental(@Valid @RequestBody Rental rental) throws URISyntaxException {
        log.debug("REST request to save Rental : {}", rental);
        if (rental.getId() != null) {
            throw new BadRequestAlertException("A new rental cannot already have an ID", ENTITY_NAME, "idexists");
        }
        rental = rentalService.save(rental);
        return ResponseEntity.created(new URI("/api/rentals/" + rental.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, rental.getId().toString()))
            .body(rental);
    }

    /**
     * {@code PUT  /rentals/:id} : Updates an existing rental.
     *
     * @param id the id of the rental to save.
     * @param rental the rental to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rental,
     * or with status {@code 400 (Bad Request)} if the rental is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rental couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Rental> updateRental(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Rental rental
    ) throws URISyntaxException {
        log.debug("REST request to update Rental : {}, {}", id, rental);
        if (rental.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rental.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rentalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        rental = rentalService.update(rental);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, rental.getId().toString()))
            .body(rental);
    }

    /**
     * {@code PATCH  /rentals/:id} : Partial updates given fields of an existing rental, field will ignore if it is null
     *
     * @param id the id of the rental to save.
     * @param rental the rental to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rental,
     * or with status {@code 400 (Bad Request)} if the rental is not valid,
     * or with status {@code 404 (Not Found)} if the rental is not found,
     * or with status {@code 500 (Internal Server Error)} if the rental couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Rental> partialUpdateRental(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Rental rental
    ) throws URISyntaxException {
        log.debug("REST request to partial update Rental partially : {}, {}", id, rental);
        if (rental.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rental.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rentalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Rental> result = rentalService.partialUpdate(rental);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, rental.getId().toString())
        );
    }

    /**
     * {@code GET  /rentals} : get all the rentals.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rentals in body.
     */
    @GetMapping("")
    public List<Rental> getAllRentals() {
        log.debug("REST request to get all Rentals");
        return rentalService.findAll();
    }

    /**
     * {@code GET  /rentals/:id} : get the "id" rental.
     *
     * @param id the id of the rental to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rental, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRental(@PathVariable("id") Long id) {
        log.debug("REST request to get Rental : {}", id);
        Optional<Rental> rental = rentalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rental);
    }

    /**
     * {@code DELETE  /rentals/:id} : delete the "id" rental.
     *
     * @param id the id of the rental to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable("id") Long id) {
        log.debug("REST request to delete Rental : {}", id);
        rentalService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
