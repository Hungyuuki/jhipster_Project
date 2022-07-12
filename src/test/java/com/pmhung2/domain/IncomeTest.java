package com.pmhung2.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.pmhung2.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IncomeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Income.class);
        Income income1 = new Income();
        income1.setId(1L);
        Income income2 = new Income();
        income2.setId(income1.getId());
        assertThat(income1).isEqualTo(income2);
        income2.setId(2L);
        assertThat(income1).isNotEqualTo(income2);
        income1.setId(null);
        assertThat(income1).isNotEqualTo(income2);
    }
}
