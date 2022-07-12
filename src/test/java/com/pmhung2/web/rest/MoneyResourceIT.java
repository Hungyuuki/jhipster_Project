package com.pmhung2.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.pmhung2.IntegrationTest;
import com.pmhung2.domain.Money;
import com.pmhung2.repository.MoneyRepository;
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
 * Integration tests for the {@link MoneyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MoneyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ROLL = "AAAAAAAAAA";
    private static final String UPDATED_ROLL = "BBBBBBBBBB";

    private static final Integer DEFAULT_INCOME = 1;
    private static final Integer UPDATED_INCOME = 2;

    private static final String ENTITY_API_URL = "/api/monies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MoneyRepository moneyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMoneyMockMvc;

    private Money money;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Money createEntity(EntityManager em) {
        Money money = new Money().name(DEFAULT_NAME).roll(DEFAULT_ROLL).income(DEFAULT_INCOME);
        return money;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Money createUpdatedEntity(EntityManager em) {
        Money money = new Money().name(UPDATED_NAME).roll(UPDATED_ROLL).income(UPDATED_INCOME);
        return money;
    }

    @BeforeEach
    public void initTest() {
        money = createEntity(em);
    }

    @Test
    @Transactional
    void createMoney() throws Exception {
        int databaseSizeBeforeCreate = moneyRepository.findAll().size();
        // Create the Money
        restMoneyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(money)))
            .andExpect(status().isCreated());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeCreate + 1);
        Money testMoney = moneyList.get(moneyList.size() - 1);
        assertThat(testMoney.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMoney.getRoll()).isEqualTo(DEFAULT_ROLL);
        assertThat(testMoney.getIncome()).isEqualTo(DEFAULT_INCOME);
    }

    @Test
    @Transactional
    void createMoneyWithExistingId() throws Exception {
        // Create the Money with an existing ID
        money.setId(1L);

        int databaseSizeBeforeCreate = moneyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMoneyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(money)))
            .andExpect(status().isBadRequest());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRollIsRequired() throws Exception {
        int databaseSizeBeforeTest = moneyRepository.findAll().size();
        // set the field null
        money.setRoll(null);

        // Create the Money, which fails.

        restMoneyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(money)))
            .andExpect(status().isBadRequest());

        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIncomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = moneyRepository.findAll().size();
        // set the field null
        money.setIncome(null);

        // Create the Money, which fails.

        restMoneyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(money)))
            .andExpect(status().isBadRequest());

        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMonies() throws Exception {
        // Initialize the database
        moneyRepository.saveAndFlush(money);

        // Get all the moneyList
        restMoneyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(money.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].roll").value(hasItem(DEFAULT_ROLL)))
            .andExpect(jsonPath("$.[*].income").value(hasItem(DEFAULT_INCOME)));
    }

    @Test
    @Transactional
    void getMoney() throws Exception {
        // Initialize the database
        moneyRepository.saveAndFlush(money);

        // Get the money
        restMoneyMockMvc
            .perform(get(ENTITY_API_URL_ID, money.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(money.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.roll").value(DEFAULT_ROLL))
            .andExpect(jsonPath("$.income").value(DEFAULT_INCOME));
    }

    @Test
    @Transactional
    void getNonExistingMoney() throws Exception {
        // Get the money
        restMoneyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMoney() throws Exception {
        // Initialize the database
        moneyRepository.saveAndFlush(money);

        int databaseSizeBeforeUpdate = moneyRepository.findAll().size();

        // Update the money
        Money updatedMoney = moneyRepository.findById(money.getId()).get();
        // Disconnect from session so that the updates on updatedMoney are not directly saved in db
        em.detach(updatedMoney);
        updatedMoney.name(UPDATED_NAME).roll(UPDATED_ROLL).income(UPDATED_INCOME);

        restMoneyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMoney.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMoney))
            )
            .andExpect(status().isOk());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeUpdate);
        Money testMoney = moneyList.get(moneyList.size() - 1);
        assertThat(testMoney.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMoney.getRoll()).isEqualTo(UPDATED_ROLL);
        assertThat(testMoney.getIncome()).isEqualTo(UPDATED_INCOME);
    }

    @Test
    @Transactional
    void putNonExistingMoney() throws Exception {
        int databaseSizeBeforeUpdate = moneyRepository.findAll().size();
        money.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMoneyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, money.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(money))
            )
            .andExpect(status().isBadRequest());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMoney() throws Exception {
        int databaseSizeBeforeUpdate = moneyRepository.findAll().size();
        money.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoneyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(money))
            )
            .andExpect(status().isBadRequest());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMoney() throws Exception {
        int databaseSizeBeforeUpdate = moneyRepository.findAll().size();
        money.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoneyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(money)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMoneyWithPatch() throws Exception {
        // Initialize the database
        moneyRepository.saveAndFlush(money);

        int databaseSizeBeforeUpdate = moneyRepository.findAll().size();

        // Update the money using partial update
        Money partialUpdatedMoney = new Money();
        partialUpdatedMoney.setId(money.getId());

        partialUpdatedMoney.name(UPDATED_NAME).roll(UPDATED_ROLL).income(UPDATED_INCOME);

        restMoneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMoney.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMoney))
            )
            .andExpect(status().isOk());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeUpdate);
        Money testMoney = moneyList.get(moneyList.size() - 1);
        assertThat(testMoney.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMoney.getRoll()).isEqualTo(UPDATED_ROLL);
        assertThat(testMoney.getIncome()).isEqualTo(UPDATED_INCOME);
    }

    @Test
    @Transactional
    void fullUpdateMoneyWithPatch() throws Exception {
        // Initialize the database
        moneyRepository.saveAndFlush(money);

        int databaseSizeBeforeUpdate = moneyRepository.findAll().size();

        // Update the money using partial update
        Money partialUpdatedMoney = new Money();
        partialUpdatedMoney.setId(money.getId());

        partialUpdatedMoney.name(UPDATED_NAME).roll(UPDATED_ROLL).income(UPDATED_INCOME);

        restMoneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMoney.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMoney))
            )
            .andExpect(status().isOk());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeUpdate);
        Money testMoney = moneyList.get(moneyList.size() - 1);
        assertThat(testMoney.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMoney.getRoll()).isEqualTo(UPDATED_ROLL);
        assertThat(testMoney.getIncome()).isEqualTo(UPDATED_INCOME);
    }

    @Test
    @Transactional
    void patchNonExistingMoney() throws Exception {
        int databaseSizeBeforeUpdate = moneyRepository.findAll().size();
        money.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMoneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, money.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(money))
            )
            .andExpect(status().isBadRequest());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMoney() throws Exception {
        int databaseSizeBeforeUpdate = moneyRepository.findAll().size();
        money.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(money))
            )
            .andExpect(status().isBadRequest());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMoney() throws Exception {
        int databaseSizeBeforeUpdate = moneyRepository.findAll().size();
        money.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoneyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(money)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Money in the database
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMoney() throws Exception {
        // Initialize the database
        moneyRepository.saveAndFlush(money);

        int databaseSizeBeforeDelete = moneyRepository.findAll().size();

        // Delete the money
        restMoneyMockMvc
            .perform(delete(ENTITY_API_URL_ID, money.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Money> moneyList = moneyRepository.findAll();
        assertThat(moneyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
