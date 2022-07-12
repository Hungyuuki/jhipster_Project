package com.pmhung2.web.rest;

import com.pmhung2.domain.Income;
import com.pmhung2.repository.IncomeRepository;
import com.pmhung2.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.pmhung2.domain.Income}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class IncomeResource {

    private final Logger log = LoggerFactory.getLogger(IncomeResource.class);

    private static final String ENTITY_NAME = "income";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IncomeRepository incomeRepository;

    public IncomeResource(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    /**
     * {@code POST  /incomes} : Create a new income.
     *
     * @param income the income to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new income, or with status {@code 400 (Bad Request)} if the income has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/incomes")
    public ResponseEntity<Income> createIncome(@Valid @RequestBody Income income) throws URISyntaxException {
        log.debug("REST request to save Income : {}", income);
        if (income.getId() != null) {
            throw new BadRequestAlertException("A new income cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Income result = incomeRepository.save(income);
        return ResponseEntity
            .created(new URI("/api/incomes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /incomes/:id} : Updates an existing income.
     *
     * @param id the id of the income to save.
     * @param income the income to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated income,
     * or with status {@code 400 (Bad Request)} if the income is not valid,
     * or with status {@code 500 (Internal Server Error)} if the income couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/incomes/{id}")
    public ResponseEntity<Income> updateIncome(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Income income
    ) throws URISyntaxException {
        log.debug("REST request to update Income : {}, {}", id, income);
        if (income.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, income.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!incomeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Income result = incomeRepository.save(income);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, income.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /incomes/:id} : Partial updates given fields of an existing income, field will ignore if it is null
     *
     * @param id the id of the income to save.
     * @param income the income to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated income,
     * or with status {@code 400 (Bad Request)} if the income is not valid,
     * or with status {@code 404 (Not Found)} if the income is not found,
     * or with status {@code 500 (Internal Server Error)} if the income couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/incomes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Income> partialUpdateIncome(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Income income
    ) throws URISyntaxException {
        log.debug("REST request to partial update Income partially : {}, {}", id, income);
        if (income.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, income.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!incomeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Income> result = incomeRepository
            .findById(income.getId())
            .map(existingIncome -> {
                if (income.getName() != null) {
                    existingIncome.setName(income.getName());
                }
                if (income.getRoll() != null) {
                    existingIncome.setRoll(income.getRoll());
                }
                if (income.getIncome() != null) {
                    existingIncome.setIncome(income.getIncome());
                }

                return existingIncome;
            })
            .map(incomeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, income.getId().toString())
        );
    }

    /**
     * {@code GET  /incomes} : get all the incomes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of incomes in body.
     */
    @GetMapping("/incomes")
    public ResponseEntity<List<Income>> getAllIncomes(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Incomes");
        Page<Income> page = incomeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /incomes/:id} : get the "id" income.
     *
     * @param id the id of the income to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the income, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/incomes/{id}")
    public ResponseEntity<Income> getIncome(@PathVariable Long id) {
        log.debug("REST request to get Income : {}", id);
        Optional<Income> income = incomeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(income);
    }

    /**
     * {@code DELETE  /incomes/:id} : delete the "id" income.
     *
     * @param id the id of the income to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/incomes/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        log.debug("REST request to delete Income : {}", id);
        incomeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
