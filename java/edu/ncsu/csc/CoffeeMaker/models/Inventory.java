package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long                   id;

    /** the ingredients for this recipe */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    /**
     * constructor for Hibernate
     */
    public Inventory () {
        this.ingredients = new ArrayList<Ingredient>();
        this.id = null;
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Add the number of ingredient units in the inventory to the current amount
     * of ingredient units.
     *
     * @param amount
     *            amount of ingredient
     * @return checked amount of ingredient
     * @throws IllegalArgumentException
     *             if the parameter isn't a positive integer
     */
    public Integer checkIngredient ( final String amount ) throws IllegalArgumentException {
        Integer amtIngredient = 0;
        try {
            amtIngredient = Integer.parseInt( amount );

        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }
        if ( amtIngredient < 0 ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }

        return amtIngredient;
    }

    /**
     * sets a specified ingredient to the given parameter
     *
     * @param ingredientToSet
     *            the new ingredient to set
     */
    public void setIngredient ( final Ingredient ingredientToSet ) {

        for ( final Ingredient ingredient : ingredients ) {
            if ( ingredient.getName().equals( ingredientToSet.getName() ) ) {
                ingredients.set( ingredients.indexOf( ingredient ),
                        new Ingredient( ingredientToSet.getName(), ingredientToSet.getAmount() ) );

            }
        }

    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        for ( final Ingredient requiredIngredient : r.getIngredients() ) {

            for ( final Ingredient inventoryIngredient : ingredients ) {
                if ( requiredIngredient.getName().equals( inventoryIngredient.getName() )
                        && inventoryIngredient.getAmount() < requiredIngredient.getAmount() ) {
                    return false; // Not enough of this ingredient in the
                                  // inventory

                }
            }

        }
        return true; // enough ingredients in system to create recipe
    }

    /**
     * gets an ingredient that matches the given ingredient type
     *
     * @return the ingredient of the ingredient type
     * @param ingredientName
     *            the found ingredient
     */
    public Ingredient getIngredientOfIngredientType ( final String ingredientName ) {
        for ( final Ingredient ingredient : ingredients ) {

            if ( ingredient.getName().equals( ingredientName ) ) {
                return ingredient;
            }

        }

        return null;
    }

    /**
     * gets the ingredients in this inventory
     *
     * @return the list of ingredients in the inventory
     */
    public List<Ingredient> getIngredients () {
        return this.ingredients;
    }

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        if ( enoughIngredients( r ) ) {
            for ( final Ingredient requiredIngredient : r.getIngredients() ) {
                for ( final Ingredient inventoryIngredient : ingredients ) {
                    if ( requiredIngredient.getName().equals( ( inventoryIngredient.getName() ) ) ) {
                        inventoryIngredient
                                .setAmount( inventoryIngredient.getAmount() - requiredIngredient.getAmount() );
                        break;
                    }
                }
            }
            return true; // Ingredients used successfully
        }
        else {
            return false;
        }
    }

    /**
     * Adds ingredients to the inventory
     *
     * @param newIngredients
     *            new ingredients to add
     * @return true if successful, false if not
     */
    public boolean addIngredients ( final List<Ingredient> newIngredients ) {
        for ( final Ingredient ingredient : newIngredients ) {
            if ( ingredient.getAmount() < 0 ) { // if initial amount < 0 return
                                                // false
                return false;
            }

        }

        for ( final Ingredient ingredient : newIngredients ) {
            boolean foundIngredient = false;
            for ( final Ingredient inventoryIngredient : ingredients ) {
                if ( ingredient.getName().equals( inventoryIngredient.getName() ) ) {
                    inventoryIngredient.setAmount( inventoryIngredient.getAmount() + ingredient.getAmount() );
                    foundIngredient = true;
                    break;
                }
            }
            if ( !foundIngredient ) {
                // If the ingredient doesn't exist in the inventory, add it
                ingredients.add( new Ingredient( ingredient.getName(), ingredient.getAmount() ) );
            }

        }
        return true; // Ingredients added successfully

    }

    @Override
    public String toString () {
        return "Inventory: " + ingredients;
    }

}
