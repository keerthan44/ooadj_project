package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.BikeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Motorbike.
 */
@Entity
@Table(name = "motorbike")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Motorbike implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "make", nullable = false)
    private String make;

    @NotNull
    @Column(name = "model", nullable = false)
    private String model;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BikeStatus status;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "motorbike")
    @JsonIgnoreProperties(value = { "customer", "motorbike" }, allowSetters = true)
    private Set<Rental> rentals = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "motorbike")
    @JsonIgnoreProperties(value = { "motorbike" }, allowSetters = true)
    private Set<Maintenance> maintenances = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Motorbike id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return this.make;
    }

    public Motorbike make(String make) {
        this.setMake(make);
        return this;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return this.model;
    }

    public Motorbike model(String model) {
        this.setModel(model);
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BikeStatus getStatus() {
        return this.status;
    }

    public Motorbike status(BikeStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BikeStatus status) {
        this.status = status;
    }

    public Set<Rental> getRentals() {
        return this.rentals;
    }

    public void setRentals(Set<Rental> rentals) {
        if (this.rentals != null) {
            this.rentals.forEach(i -> i.setMotorbike(null));
        }
        if (rentals != null) {
            rentals.forEach(i -> i.setMotorbike(this));
        }
        this.rentals = rentals;
    }

    public Motorbike rentals(Set<Rental> rentals) {
        this.setRentals(rentals);
        return this;
    }

    public Motorbike addRental(Rental rental) {
        this.rentals.add(rental);
        rental.setMotorbike(this);
        return this;
    }

    public Motorbike removeRental(Rental rental) {
        this.rentals.remove(rental);
        rental.setMotorbike(null);
        return this;
    }

    public Set<Maintenance> getMaintenances() {
        return this.maintenances;
    }

    public void setMaintenances(Set<Maintenance> maintenances) {
        if (this.maintenances != null) {
            this.maintenances.forEach(i -> i.setMotorbike(null));
        }
        if (maintenances != null) {
            maintenances.forEach(i -> i.setMotorbike(this));
        }
        this.maintenances = maintenances;
    }

    public Motorbike maintenances(Set<Maintenance> maintenances) {
        this.setMaintenances(maintenances);
        return this;
    }

    public Motorbike addMaintenance(Maintenance maintenance) {
        this.maintenances.add(maintenance);
        maintenance.setMotorbike(this);
        return this;
    }

    public Motorbike removeMaintenance(Maintenance maintenance) {
        this.maintenances.remove(maintenance);
        maintenance.setMotorbike(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Motorbike)) {
            return false;
        }
        return getId() != null && getId().equals(((Motorbike) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Motorbike{" +
            "id=" + getId() +
            ", make='" + getMake() + "'" +
            ", model='" + getModel() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
