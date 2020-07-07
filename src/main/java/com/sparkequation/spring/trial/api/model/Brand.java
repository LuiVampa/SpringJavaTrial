package com.sparkequation.spring.trial.api.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "BRAND")
public class Brand {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "BRAND_SEQ")
    @SequenceGenerator(name = "BRAND_SEQ", sequenceName = "BRAND_SEQ", allocationSize = 1, initialValue = 11)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "COUNTRY")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brand brand = (Brand) o;
        return id == brand.id &&
               name.equals(brand.name) &&
               Objects.equals(country, brand.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, country);
    }

    private int id;
    private String name;
    private String country;
}
