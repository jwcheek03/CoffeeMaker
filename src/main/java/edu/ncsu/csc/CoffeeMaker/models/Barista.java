package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

/**
 * Represents a Barista stored in the CoffeeMaker system
 *
 * @author Olivia Nowell
 *
 */
@Entity
@JsonIgnoreProperties ( value = { "password" } )
public class Barista extends User {

    /** Barista's orders */
    @OneToMany ( mappedBy = "barista" )
    private List<Order> orders;

    public Barista () {

    }

    /**
     * Creates a Barista from the provided UserForm
     *
     * @param uf
     *            UserForm to build a barista
     */
    public Barista ( final UserForm uf ) {
        super( uf );
        orders = new ArrayList<>();
        if ( !getRoles().contains( Role.ROLE_BARISTA ) ) {
            throw new IllegalArgumentException( "Attempted to create a Barista record for a non-barista user!" );
        }
    }

    public List<Order> getOrders () {
        return orders;
    }

    /**
     * Marks the Barista's order as fufilled
     *
     * @param order
     *            order to update from
     */
    public void makeOrder ( final Order order ) {
        // set other order properties once Order class made
        order.updateStatus();
        order.setBarista( this );
        orders.add( order );
    }

}
