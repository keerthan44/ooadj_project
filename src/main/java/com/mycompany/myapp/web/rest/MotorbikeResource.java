package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Motorbike;
import com.mycompany.myapp.repository.MotorbikeRepository;
import com.mycompany.myapp.service.MotorbikeService;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Motorbike}.
 */
@RestController
@RequestMapping("/api/motorbikes")
public class MotorbikeResource {

    private final Logger log = LoggerFactory.getLogger(MotorbikeResource.class);

    private static final String ENTITY_NAME = "motorbike";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MotorbikeService motorbikeService;

    private final MotorbikeRepository motorbikeRepository;

    public MotorbikeResource(MotorbikeService motorbikeService, MotorbikeRepository motorbikeRepository) {
        this.motorbikeService = motorbikeService;
        this.motorbikeRepository = motorbikeRepository;
    }

    /**
     * {@code POST  /motorbikes} : Create a new motorbike.
     *
     * @param motorbike the motorbike to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new motorbike, or with status {@code 400 (Bad Request)} if the motorbike has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Motorbike> createMotorbike(@Valid @RequestBody Motorbike motorbike) throws URISyntaxException {
        log.debug("REST request to save Motorbike : {}", motorbike);
        if (motorbike.getId() != null) {
            throw new BadRequestAlertException("A new motorbike cannot already have an ID", ENTITY_NAME, "idexists");
        }
        motorbike = motorbikeService.save(motorbike);
        return ResponseEntity.created(new URI("/api/motorbikes/" + motorbike.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, motorbike.getId().toString()))
            .body(motorbike);
    }

    /**
     * {@code PUT  /motorbikes/:id} : Updates an existing motorbike.
     *
     * @param id the id of the motorbike to save.
     * @param motorbike the motorbike to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated motorbike,
     * or with status {@code 400 (Bad Request)} if the motorbike is not valid,
     * or with status {@code 500 (Internal Server Error)} if the motorbike couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Motorbike> updateMotorbike(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Motorbike motorbike
    ) throws URISyntaxException {
        log.debug("REST request to update Motorbike : {}, {}", id, motorbike);
        if (motorbike.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, motorbike.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!motorbikeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        motorbike = motorbikeService.update(motorbike);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, motorbike.getId().toString()))
            .body(motorbike);
    }

    /**
     * {@code PATCH  /motorbikes/:id} : Partial updates given fields of an existing motorbike, field will ignore if it is null
     *
     * @param id the id of the motorbike to save.
     * @param motorbike the motorbike to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated motorbike,
     * or with status {@code 400 (Bad Request)} if the motorbike is not valid,
     * or with status {@code 404 (Not Found)} if the motorbike is not found,
     * or with status {@code 500 (Internal Server Error)} if the motorbike couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Motorbike> partialUpdateMotorbike(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Motorbike motorbike
    ) throws URISyntaxException {
        log.debug("REST request to partial update Motorbike partially : {}, {}", id, motorbike);
        if (motorbike.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, motorbike.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!motorbikeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Motorbike> result = motorbikeService.partialUpdate(motorbike);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, motorbike.getId().toString())
        );
    }

    /**
     * {@code GET  /motorbikes} : get all the motorbikes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of motorbikes in body.
     */
    @GetMapping("")
    public List<Motorbike> getAllMotorbikes() {
        log.debug("REST request to get all Motorbikes");
        return motorbikeService.findAll();
    }

    /**
     * {@code GET  /motorbikes/:id} : get the "id" motorbike.
     *
     * @param id the id of the motorbike to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the motorbike, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Motorbike> getMotorbike(@PathVariable("id") Long id) {
        log.debug("REST request to get Motorbike : {}", id);
        Optional<Motorbike> motorbike = motorbikeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(motorbike);
    }

    /**
     * {@code DELETE  /motorbikes/:id} : delete the "id" motorbike.
     *
     * @param id the id of the motorbike to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMotorbike(@PathVariable("id") Long id) {
        log.debug("REST request to delete Motorbike : {}", id);
        motorbikeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
