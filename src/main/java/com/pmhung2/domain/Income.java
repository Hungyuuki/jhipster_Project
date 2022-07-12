package com.pmhung2.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Income.
 */
@Entity
@Table(name = "income")
public class Income implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "roll")
    private String roll;

    @NotNull
    @Column(name = "income", nullable = false)
    private Integer income;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Income id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Income name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoll() {
        return this.roll;
    }

    public Income roll(String roll) {
        this.setRoll(roll);
        return this;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public Integer getIncome() {
        return this.income;
    }

    public Income income(Integer income) {
        this.setIncome(income);
        return this;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Income)) {
            return false;
        }
        return id != null && id.equals(((Income) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Income{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", roll='" + getRoll() + "'" +
            ", income=" + getIncome() +
            "}";
    }
}
