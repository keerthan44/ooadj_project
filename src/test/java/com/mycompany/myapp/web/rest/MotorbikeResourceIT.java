package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MotorbikeAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Motorbike;
import com.mycompany.myapp.domain.enumeration.BikeStatus;
import com.mycompany.myapp.repository.MotorbikeRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MotorbikeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MotorbikeResourceIT {

    private static final String DEFAULT_MAKE = "AAAAAAAAAA";
    private static final String UPDATED_MAKE = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final BikeStatus DEFAULT_STATUS = BikeStatus.AVAILABLE;
    private static final BikeStatus UPDATED_STATUS = BikeStatus.RENTED;

    private static final String ENTITY_API_URL = "/api/motorbikes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MotorbikeRepository motorbikeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMotorbikeMockMvc;

    private Motorbike motorbike;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Motorbike createEntity(EntityManager em) {
        Motorbike motorbike = new Motorbike().make(DEFAULT_MAKE).model(DEFAULT_MODEL).status(DEFAULT_STATUS);
        return motorbike;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Motorbike createUpdatedEntity(EntityManager em) {
        Motorbike motorbike = new Motorbike().make(UPDATED_MAKE).model(UPDATED_MODEL).status(UPDATED_STATUS);
        return motorbike;
    }

    @BeforeEach
    public void initTest() {
        motorbike = createEntity(em);
    }

    @Test
    @Transactional
    void createMotorbike() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Motorbike
        var returnedMotorbike = om.readValue(
            restMotorbikeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(motorbike)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Motorbike.class
        );

        // Validate the Motorbike in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMotorbikeUpdatableFieldsEquals(returnedMotorbike, getPersistedMotorbike(returnedMotorbike));
    }

    @Test
    @Transactional
    void createMotorbikeWithExistingId() throws Exception {
        // Create the Motorbike with an existing ID
        motorbike.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMotorbikeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(motorbike)))
            .andExpect(status().isBadRequest());

        // Validate the Motorbike in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMakeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        motorbike.setMake(null);

        // Create the Motorbike, which fails.

        restMotorbikeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(motorbike)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        motorbike.setModel(null);

        // Create the Motorbike, which fails.

        restMotorbikeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(motorbike)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        motorbike.setStatus(null);

        // Create the Motorbike, which fails.

        restMotorbikeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(motorbike)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMotorbikes() throws Exception {
        // Initialize the database
        motorbikeRepository.saveAndFlush(motorbike);

        // Get all the motorbikeList
        restMotorbikeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(motorbike.getId().intValue())))
            .andExpect(jsonPath("$.[*].make").value(hasItem(DEFAULT_MAKE)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getMotorbike() throws Exception {
        // Initialize the database
        motorbikeRepository.saveAndFlush(motorbike);

        // Get the motorbike
        restMotorbikeMockMvc
            .perform(get(ENTITY_API_URL_ID, motorbike.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(motorbike.getId().intValue()))
            .andExpect(jsonPath("$.make").value(DEFAULT_MAKE))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMotorbike() throws Exception {
        // Get the motorbike
        restMotorbikeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMotorbike() throws Exception {
        // Initialize the database
        motorbikeRepository.saveAndFlush(motorbike);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the motorbike
        Motorbike updatedMotorbike = motorbikeRepository.findById(motorbike.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMotorbike are not directly saved in db
        em.detach(updatedMotorbike);
        updatedMotorbike.make(UPDATED_MAKE).model(UPDATED_MODEL).status(UPDATED_STATUS);

        restMotorbikeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMotorbike.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMotorbike))
            )
            .andExpect(status().isOk());

        // Validate the Motorbike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMotorbikeToMatchAllProperties(updatedMotorbike);
    }

    @Test
    @Transactional
    void putNonExistingMotorbike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        motorbike.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMotorbikeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, motorbike.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(motorbike))
            )
            .andExpect(status().isBadRequest());

        // Validate the Motorbike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMotorbike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        motorbike.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMotorbikeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(motorbike))
            )
            .andExpect(status().isBadRequest());

        // Validate the Motorbike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMotorbike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        motorbike.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMotorbikeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(motorbike)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Motorbike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMotorbikeWithPatch() throws Exception {
        // Initialize the database
        motorbikeRepository.saveAndFlush(motorbike);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the motorbike using partial update
        Motorbike partialUpdatedMotorbike = new Motorbike();
        partialUpdatedMotorbike.setId(motorbike.getId());

        partialUpdatedMotorbike.status(UPDATED_STATUS);

        restMotorbikeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMotorbike.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMotorbike))
            )
            .andExpect(status().isOk());

        // Validate the Motorbike in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMotorbikeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMotorbike, motorbike),
            getPersistedMotorbike(motorbike)
        );
    }

    @Test
    @Transactional
    void fullUpdateMotorbikeWithPatch() throws Exception {
        // Initialize the database
        motorbikeRepository.saveAndFlush(motorbike);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the motorbike using partial update
        Motorbike partialUpdatedMotorbike = new Motorbike();
        partialUpdatedMotorbike.setId(motorbike.getId());

        partialUpdatedMotorbike.make(UPDATED_MAKE).model(UPDATED_MODEL).status(UPDATED_STATUS);

        restMotorbikeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMotorbike.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMotorbike))
            )
            .andExpect(status().isOk());

        // Validate the Motorbike in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMotorbikeUpdatableFieldsEquals(partialUpdatedMotorbike, getPersistedMotorbike(partialUpdatedMotorbike));
    }

    @Test
    @Transactional
    void patchNonExistingMotorbike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        motorbike.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMotorbikeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, motorbike.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(motorbike))
            )
            .andExpect(status().isBadRequest());

        // Validate the Motorbike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMotorbike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        motorbike.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMotorbikeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(motorbike))
            )
            .andExpect(status().isBadRequest());

        // Validate the Motorbike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMotorbike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        motorbike.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMotorbikeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(motorbike)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Motorbike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMotorbike() throws Exception {
        // Initialize the database
        motorbikeRepository.saveAndFlush(motorbike);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the motorbike
        restMotorbikeMockMvc
            .perform(delete(ENTITY_API_URL_ID, motorbike.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return motorbikeRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Motorbike getPersistedMotorbike(Motorbike motorbike) {
        return motorbikeRepository.findById(motorbike.getId()).orElseThrow();
    }

    protected void assertPersistedMotorbikeToMatchAllProperties(Motorbike expectedMotorbike) {
        assertMotorbikeAllPropertiesEquals(expectedMotorbike, getPersistedMotorbike(expectedMotorbike));
    }

    protected void assertPersistedMotorbikeToMatchUpdatableProperties(Motorbike expectedMotorbike) {
        assertMotorbikeAllUpdatablePropertiesEquals(expectedMotorbike, getPersistedMotorbike(expectedMotorbike));
    }
}
