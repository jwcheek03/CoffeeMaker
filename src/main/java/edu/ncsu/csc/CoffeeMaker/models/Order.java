package edu.ncsu.csc.CoffeeMaker.models;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;

/**
 * Order for the coffee maker. Order is tied to the database using Hibernate
 * libraries.
 *
 * @author John Shockley
 */
@Entity
@Table ( name = "beverage_order" )
public class Order extends DomainObject {

    /** Order id */
    @Id
    @GeneratedValue
    private Long          id;

    /** the recipes for this order */
    @OneToOne
    @NotNull
    private Recipe        recipe;

    /** Order status */
    @Enumerated ( EnumType.STRING )
    private OrderStatus   status;

    /** username of owner of this order */
    @NotBlank
    private String        ownerUserName;

    /** time stamp of when the current status occurred */
    private LocalDateTime statusTimestamp;

    /** bidirectional relationship between an Order and a Barista */
    @ManyToOne
    private Barista     barista;

    /** bidirectional relationship between an Order and a Customer */
    @ManyToOne
    private Customer    customer;

    /**
     * Creates a default order for the coffee maker.
     */
    public Order () {
        setRecipe( new Recipe() );
        setStatus( OrderStatus.ORDERSTARTED );
        setOwnerUserName( "" );
        setStatusTimestamp( LocalDateTime.now() );
    }

    /**
     * gets the current status time stamp
     *
     * @return the time stamp
     */
    public LocalDateTime getStatusTimestamp () {
        return statusTimestamp;
    }

    /**
     * sets the current status time stamp
     *
     * @param statusTimestamp
     *            the time to update the order with
     */
    private void setStatusTimestamp ( final LocalDateTime statusTimestamp ) {
        this.statusTimestamp = statusTimestamp;
    }

    /**
     * @return the id
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * @return the status
     */
    public OrderStatus getStatus () {
        return status;
    }

    /**
     * sets initial status. This is only used upon instantiation. To update
     * order status, use updateStatus() method
     *
     * @param status
     *            the status to set
     */
    private void setStatus ( final OrderStatus newStatus ) {

        this.status = newStatus;
    }

    /**
     * updates status to be the next status in order. Status starts as 'started'
     * then 'fulfilled' then 'received'. Once received, the status should not be
     * able to be updated. Additionally, updates the status time stamp to
     * represent the time when the current status has occurred
     *
     * @param status
     *            the status to set
     * @return true if status has been updated legally
     */
    public boolean updateStatus () {
        statusTimestamp = LocalDateTime.now();
        if ( status == OrderStatus.ORDERSTARTED ) {
            status = OrderStatus.ORDERREADYFORPICKUP;
            return true;
        }
        else if ( status == OrderStatus.ORDERREADYFORPICKUP ) {
            status = OrderStatus.ORDERCLOSED;
            return true;
        }
        return false;
    }

    /**
     * @return the Barista assigned to this order
     */
    public Barista getBarista () {
        return barista;
    }

    /**
     * @param the
     *            Customer the Customer to set
     */
    public void setCustomer ( final Customer customer ) {
        this.customer = customer;
    }

    /**
     * @return the Customer assigned to this order
     */
    public Customer getCustomer () {
        return customer;
    }

    /**
     * @param the
     *            Barista the Barista to set
     */
    public void setBarista ( final Barista barista ) {
        this.barista = barista;
    }

    /**
     * @return the ownerUserName
     */
    public String getOwnerUserName () {
        return ownerUserName;
    }

    /**
     * @param ownerUserName
     *            the ownerUserName to set
     */
    public void setOwnerUserName ( final String ownerUserName ) {
        this.ownerUserName = ownerUserName;
    }

    /**
     * @return the recipes
     */
    public Recipe getRecipe () {
        return this.recipe;
    }

    /**
     * sets the recipe
     *
     * @param newRecipe
     *            recipe to set
     */
    public void setRecipe ( final Recipe newRecipe ) {
        this.recipe = newRecipe;
    }

    @Override
    public String toString () {
        return "Order id " + id + " for username: " + ownerUserName + ". Recipe=" + recipe.toString() + "status="
                + status;
    }

    @Override
    public int hashCode () {
        return Objects.hash( ownerUserName, recipe, status );
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Order other = (Order) obj;
        return Objects.equals( ownerUserName, other.ownerUserName ) && Objects.equals( recipe, other.recipe )
                && status == other.status;
    }

}
