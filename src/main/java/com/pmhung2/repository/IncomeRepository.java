package com.pmhung2.repository;

import com.pmhung2.domain.Income;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Income entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {}
