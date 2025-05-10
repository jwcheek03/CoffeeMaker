package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

/**
 * Ingredient Class holds the Ingredient Object that represents ingredients in a
 * recipe.
 */
@Entity
public class Ingredient extends DomainObject {

    /**
     * Empty Ingredient Constructor that will be used by Hibernate.
     */
    public Ingredient () {

    }

    /**
     * Actual Ingredient constructor that represents an Ingredient with a name
     * and an amount.
     *
     * @param name
     *            name of the ingredient
     * @param amount
     *            the ingredient's amount
     */
    public Ingredient ( final String name, @Min ( 0 ) final Integer amount ) {

        this.name = name;
        this.amount = amount;
    }

    /** Ingredient id */
    @Id
    @GeneratedValue
    private Long    id;

    /** Ingredient amount */
    @Min ( 0 )
    private Integer amount;

    /** Ingredient name */
    private String  name;

    /**
     * Returns the id of the ingredient
     */
    @Override
    public Long getId () {

        return id;
    }

    /**
     * Returns the name of the Ingredient
     *
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of the Ingredient
     *
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the Ingredient's amount
     *
     * @return the amount
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * Sets the Ingredient's amount
     *
     * @param amount
     *            the amount to set
     */
    public void setAmount ( final Integer amount ) {
        this.amount = amount;
    }

    /**
     * Set the ID of the Ingredient (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    @Override
    public String toString () {
        return "[name=" + name + ", amount=" + amount + "]";
    }

}
