package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

/**
 * Represents a Customer stored in the CoffeeMaker system
 *
 * @author Olivia Nowell
 *
 */
@Entity
@JsonIgnoreProperties ( value = { "password" } )
public class Customer extends User {

    /** Customer's orders */
    @OneToMany ( mappedBy = "customer" )
    private List<Order> orders;

    public Customer () {

    }

    /**
     * Creates a Customer from the provided UserForm
     *
     * @param uf
     *            UserForm to build a customer
     */
    public Customer ( final UserForm uf ) {
        super( uf );
        orders = new ArrayList<>();
        if ( !getRoles().contains( Role.ROLE_CUSTOMER ) ) {
            throw new IllegalArgumentException( "Attempted to create a Customer record for a non-customer user!" );
        }

    }

    public List<Order> getOrders () {
        return orders;
    }

    /**
     * Creates an order for the customer from chosen recipe
     *
     * @param recipe
     *            recipe to update from
     * @return new order
     */
    public Order orderDrink ( final Recipe recipe ) {
        // set other order properties once Order class made
        final Order newOrder = new Order();
        newOrder.setRecipe( recipe );
        // set OwnerUserName kind of becomes obsolete with customer mapping
        newOrder.setOwnerUserName( this.getUsername() );
        newOrder.setCustomer( this );
        orders.add( newOrder );
        return newOrder;
    }
}
