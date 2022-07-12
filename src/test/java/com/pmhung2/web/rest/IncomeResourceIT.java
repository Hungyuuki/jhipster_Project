package com.pmhung2.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.pmhung2.IntegrationTest;
import com.pmhung2.domain.Income;
import com.pmhung2.repository.IncomeRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link IncomeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IncomeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ROLL = "AAAAAAAAAA";
    private static final String UPDATED_ROLL = "BBBBBBBBBB";

    private static final Integer DEFAULT_INCOME = 1;
    private static final Integer UPDATED_INCOME = 2;

    private static final String ENTITY_API_URL = "/api/incomes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIncomeMockMvc;

    private Income income;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Income createEntity(EntityManager em) {
        Income income = new Income().name(DEFAULT_NAME).roll(DEFAULT_ROLL).income(DEFAULT_INCOME);
        return income;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Income createUpdatedEntity(EntityManager em) {
        Income income = new Income().name(UPDATED_NAME).roll(UPDATED_ROLL).income(UPDATED_INCOME);
        return income;
    }

    @BeforeEach
    public void initTest() {
        income = createEntity(em);
    }

    @Test
    @Transactional
    void createIncome() throws Exception {
        int databaseSizeBeforeCreate = incomeRepository.findAll().size();
        // Create the Income
        restIncomeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(income)))
            .andExpect(status().isCreated());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeCreate + 1);
        Income testIncome = incomeList.get(incomeList.size() - 1);
        assertThat(testIncome.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIncome.getRoll()).isEqualTo(DEFAULT_ROLL);
        assertThat(testIncome.getIncome()).isEqualTo(DEFAULT_INCOME);
    }

    @Test
    @Transactional
    void createIncomeWithExistingId() throws Exception {
        // Create the Income with an existing ID
        income.setId(1L);

        int databaseSizeBeforeCreate = incomeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIncomeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(income)))
            .andExpect(status().isBadRequest());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIncomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = incomeRepository.findAll().size();
        // set the field null
        income.setIncome(null);

        // Create the Income, which fails.

        restIncomeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(income)))
            .andExpect(status().isBadRequest());

        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIncomes() throws Exception {
        // Initialize the database
        incomeRepository.saveAndFlush(income);

        // Get all the incomeList
        restIncomeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(income.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].roll").value(hasItem(DEFAULT_ROLL)))
            .andExpect(jsonPath("$.[*].income").value(hasItem(DEFAULT_INCOME)));
    }

    @Test
    @Transactional
    void getIncome() throws Exception {
        // Initialize the database
        incomeRepository.saveAndFlush(income);

        // Get the income
        restIncomeMockMvc
            .perform(get(ENTITY_API_URL_ID, income.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(income.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.roll").value(DEFAULT_ROLL))
            .andExpect(jsonPath("$.income").value(DEFAULT_INCOME));
    }

    @Test
    @Transactional
    void getNonExistingIncome() throws Exception {
        // Get the income
        restIncomeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewIncome() throws Exception {
        // Initialize the database
        incomeRepository.saveAndFlush(income);

        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();

        // Update the income
        Income updatedIncome = incomeRepository.findById(income.getId()).get();
        // Disconnect from session so that the updates on updatedIncome are not directly saved in db
        em.detach(updatedIncome);
        updatedIncome.name(UPDATED_NAME).roll(UPDATED_ROLL).income(UPDATED_INCOME);

        restIncomeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedIncome.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedIncome))
            )
            .andExpect(status().isOk());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
        Income testIncome = incomeList.get(incomeList.size() - 1);
        assertThat(testIncome.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIncome.getRoll()).isEqualTo(UPDATED_ROLL);
        assertThat(testIncome.getIncome()).isEqualTo(UPDATED_INCOME);
    }

    @Test
    @Transactional
    void putNonExistingIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();
        income.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIncomeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, income.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(income))
            )
            .andExpect(status().isBadRequest());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();
        income.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncomeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(income))
            )
            .andExpect(status().isBadRequest());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();
        income.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncomeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(income)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIncomeWithPatch() throws Exception {
        // Initialize the database
        incomeRepository.saveAndFlush(income);

        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();

        // Update the income using partial update
        Income partialUpdatedIncome = new Income();
        partialUpdatedIncome.setId(income.getId());

        partialUpdatedIncome.roll(UPDATED_ROLL).income(UPDATED_INCOME);

        restIncomeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIncome.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIncome))
            )
            .andExpect(status().isOk());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
        Income testIncome = incomeList.get(incomeList.size() - 1);
        assertThat(testIncome.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIncome.getRoll()).isEqualTo(UPDATED_ROLL);
        assertThat(testIncome.getIncome()).isEqualTo(UPDATED_INCOME);
    }

    @Test
    @Transactional
    void fullUpdateIncomeWithPatch() throws Exception {
        // Initialize the database
        incomeRepository.saveAndFlush(income);

        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();

        // Update the income using partial update
        Income partialUpdatedIncome = new Income();
        partialUpdatedIncome.setId(income.getId());

        partialUpdatedIncome.name(UPDATED_NAME).roll(UPDATED_ROLL).income(UPDATED_INCOME);

        restIncomeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIncome.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIncome))
            )
            .andExpect(status().isOk());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
        Income testIncome = incomeList.get(incomeList.size() - 1);
        assertThat(testIncome.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIncome.getRoll()).isEqualTo(UPDATED_ROLL);
        assertThat(testIncome.getIncome()).isEqualTo(UPDATED_INCOME);
    }

    @Test
    @Transactional
    void patchNonExistingIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();
        income.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIncomeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, income.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(income))
            )
            .andExpect(status().isBadRequest());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();
        income.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncomeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(income))
            )
            .andExpect(status().isBadRequest());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();
        income.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIncomeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(income)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIncome() throws Exception {
        // Initialize the database
        incomeRepository.saveAndFlush(income);

        int databaseSizeBeforeDelete = incomeRepository.findAll().size();

        // Delete the income
        restIncomeMockMvc
            .perform(delete(ENTITY_API_URL_ID, income.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Income> incomeList = incomeRepository.findAll();
        assertThat(incomeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
