package edu.ncsu.csc.CoffeeMaker.models.enums;

/**
 * an ENUM for the status of an order. An order can have the status of either
 * ORDERSTARTED, ORDERREADYFORPICKUP, or ORDERCLOSED
 *
 * @author John Shockley
 */
public enum OrderStatus {
    /**
     * ORDERSTARTED represents that an order has just been created by a customer
     */
    ORDERSTARTED,
    /**
     * ORDERREADYFORPICKUP represents that an order has been made by a staff and
     * is ready to be picked up by a customer
     */
    ORDERREADYFORPICKUP,
    /**
     * ORDERCLOSED represents that an order has been picked up by a customer and
     * is now closed
     */
    ORDERCLOSED
}
