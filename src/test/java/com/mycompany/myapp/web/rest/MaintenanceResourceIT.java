package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MaintenanceAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Maintenance;
import com.mycompany.myapp.repository.MaintenanceRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link MaintenanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MaintenanceResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/maintenances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMaintenanceMockMvc;

    private Maintenance maintenance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Maintenance createEntity(EntityManager em) {
        Maintenance maintenance = new Maintenance()
            .description(DEFAULT_DESCRIPTION)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return maintenance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Maintenance createUpdatedEntity(EntityManager em) {
        Maintenance maintenance = new Maintenance()
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        return maintenance;
    }

    @BeforeEach
    public void initTest() {
        maintenance = createEntity(em);
    }

    @Test
    @Transactional
    void createMaintenance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Maintenance
        var returnedMaintenance = om.readValue(
            restMaintenanceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(maintenance)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Maintenance.class
        );

        // Validate the Maintenance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMaintenanceUpdatableFieldsEquals(returnedMaintenance, getPersistedMaintenance(returnedMaintenance));
    }

    @Test
    @Transactional
    void createMaintenanceWithExistingId() throws Exception {
        // Create the Maintenance with an existing ID
        maintenance.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaintenanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(maintenance)))
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        maintenance.setStartDate(null);

        // Create the Maintenance, which fails.

        restMaintenanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(maintenance)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMaintenances() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        // Get all the maintenanceList
        restMaintenanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(maintenance.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    void getMaintenance() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        // Get the maintenance
        restMaintenanceMockMvc
            .perform(get(ENTITY_API_URL_ID, maintenance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(maintenance.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMaintenance() throws Exception {
        // Get the maintenance
        restMaintenanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMaintenance() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenance
        Maintenance updatedMaintenance = maintenanceRepository.findById(maintenance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMaintenance are not directly saved in db
        em.detach(updatedMaintenance);
        updatedMaintenance.description(UPDATED_DESCRIPTION).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restMaintenanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMaintenance.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMaintenance))
            )
            .andExpect(status().isOk());

        // Validate the Maintenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMaintenanceToMatchAllProperties(updatedMaintenance);
    }

    @Test
    @Transactional
    void putNonExistingMaintenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenance.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, maintenance.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(maintenance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMaintenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(maintenance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMaintenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(maintenance)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Maintenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMaintenanceWithPatch() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenance using partial update
        Maintenance partialUpdatedMaintenance = new Maintenance();
        partialUpdatedMaintenance.setId(maintenance.getId());

        partialUpdatedMaintenance.description(UPDATED_DESCRIPTION).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restMaintenanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaintenance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMaintenance))
            )
            .andExpect(status().isOk());

        // Validate the Maintenance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaintenanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMaintenance, maintenance),
            getPersistedMaintenance(maintenance)
        );
    }

    @Test
    @Transactional
    void fullUpdateMaintenanceWithPatch() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenance using partial update
        Maintenance partialUpdatedMaintenance = new Maintenance();
        partialUpdatedMaintenance.setId(maintenance.getId());

        partialUpdatedMaintenance.description(UPDATED_DESCRIPTION).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restMaintenanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaintenance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMaintenance))
            )
            .andExpect(status().isOk());

        // Validate the Maintenance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaintenanceUpdatableFieldsEquals(partialUpdatedMaintenance, getPersistedMaintenance(partialUpdatedMaintenance));
    }

    @Test
    @Transactional
    void patchNonExistingMaintenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenance.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, maintenance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(maintenance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMaintenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(maintenance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMaintenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(maintenance)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Maintenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMaintenance() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the maintenance
        restMaintenanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, maintenance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return maintenanceRepository.count();
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

    protected Maintenance getPersistedMaintenance(Maintenance maintenance) {
        return maintenanceRepository.findById(maintenance.getId()).orElseThrow();
    }

    protected void assertPersistedMaintenanceToMatchAllProperties(Maintenance expectedMaintenance) {
        assertMaintenanceAllPropertiesEquals(expectedMaintenance, getPersistedMaintenance(expectedMaintenance));
    }

    protected void assertPersistedMaintenanceToMatchUpdatableProperties(Maintenance expectedMaintenance) {
        assertMaintenanceAllUpdatablePropertiesEquals(expectedMaintenance, getPersistedMaintenance(expectedMaintenance));
    }
}
