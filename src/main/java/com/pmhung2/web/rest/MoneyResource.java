package com.pmhung2.web.rest;

import com.pmhung2.domain.Money;
import com.pmhung2.repository.MoneyRepository;
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
 * REST controller for managing {@link com.pmhung2.domain.Money}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MoneyResource {

    private final Logger log = LoggerFactory.getLogger(MoneyResource.class);

    private static final String ENTITY_NAME = "money";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MoneyRepository moneyRepository;

    public MoneyResource(MoneyRepository moneyRepository) {
        this.moneyRepository = moneyRepository;
    }

    /**
     * {@code POST  /monies} : Create a new money.
     *
     * @param money the money to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new money, or with status {@code 400 (Bad Request)} if the money has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/monies")
    public ResponseEntity<Money> createMoney(@Valid @RequestBody Money money) throws URISyntaxException {
        log.debug("REST request to save Money : {}", money);
        if (money.getId() != null) {
            throw new BadRequestAlertException("A new money cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Money result = moneyRepository.save(money);
        return ResponseEntity
            .created(new URI("/api/monies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /monies/:id} : Updates an existing money.
     *
     * @param id the id of the money to save.
     * @param money the money to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated money,
     * or with status {@code 400 (Bad Request)} if the money is not valid,
     * or with status {@code 500 (Internal Server Error)} if the money couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/monies/{id}")
    public ResponseEntity<Money> updateMoney(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Money money)
        throws URISyntaxException {
        log.debug("REST request to update Money : {}, {}", id, money);
        if (money.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, money.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!moneyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Money result = moneyRepository.save(money);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, money.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /monies/:id} : Partial updates given fields of an existing money, field will ignore if it is null
     *
     * @param id the id of the money to save.
     * @param money the money to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated money,
     * or with status {@code 400 (Bad Request)} if the money is not valid,
     * or with status {@code 404 (Not Found)} if the money is not found,
     * or with status {@code 500 (Internal Server Error)} if the money couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/monies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Money> partialUpdateMoney(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Money money
    ) throws URISyntaxException {
        log.debug("REST request to partial update Money partially : {}, {}", id, money);
        if (money.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, money.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!moneyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Money> result = moneyRepository
            .findById(money.getId())
            .map(existingMoney -> {
                if (money.getName() != null) {
                    existingMoney.setName(money.getName());
                }
                if (money.getRoll() != null) {
                    existingMoney.setRoll(money.getRoll());
                }
                if (money.getIncome() != null) {
                    existingMoney.setIncome(money.getIncome());
                }

                return existingMoney;
            })
            .map(moneyRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, money.getId().toString())
        );
    }

    /**
     * {@code GET  /monies} : get all the monies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of monies in body.
     */
    @GetMapping("/monies")
    public ResponseEntity<List<Money>> getAllMonies(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Monies");
        Page<Money> page = moneyRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /monies/:id} : get the "id" money.
     *
     * @param id the id of the money to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the money, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/monies/{id}")
    public ResponseEntity<Money> getMoney(@PathVariable Long id) {
        log.debug("REST request to get Money : {}", id);
        Optional<Money> money = moneyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(money);
    }

    /**
     * {@code DELETE  /monies/:id} : delete the "id" money.
     *
     * @param id the id of the money to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/monies/{id}")
    public ResponseEntity<Void> deleteMoney(@PathVariable Long id) {
        log.debug("REST request to delete Money : {}", id);
        moneyRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
