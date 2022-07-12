package com.pmhung2.repository;

import com.pmhung2.domain.Money;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Money entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MoneyRepository extends JpaRepository<Money, Long> {}
