package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for the Order.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author John Shockley
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIOrderController extends APIController {

    /**
     * orderService object, to be autowired in by Spring to allow for
     * manipulating the Order model
     */
    @Autowired
    private OrderService     orderService;

    /**
     * recipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private RecipeService    recipeService;

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService inventoryService;

    /**
     * REST API method to provide GET access to all orders in the system
     *
     * @return JSON representation of all orders
     */
    @GetMapping ( BASE_PATH + "/orders" )
    public List<Order> getOrders () {
        return orderService.findAll();
    }

    /**
     * REST API method to provide GET access to a specific order, as indicated
     * by the path variable provided (the id of the order desired)
     *
     * @param id
     *            order id
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/orders/{id}" )
    public ResponseEntity getOrder ( @PathVariable ( "id" ) final long id ) {
        final Order order = orderService.findById( id );
        return null == order
                ? new ResponseEntity( errorResponse( "No order found with id " + id ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( order, HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Order model. This is used
     * to create a new Order by automatically converting the JSON RequestBody
     * provided to an Order object. Invalid JSON will fail. Takes apart the
     * provided JSON to create a new order. This is to ensure proper orderStatus
     * is set.
     *
     * @param order
     *            The valid Order to be saved.
     * @return ResponseEntity indicating success if the Order could be saved, or
     *         an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/orders" )
    public ResponseEntity createOrder ( @RequestBody final Order order ) {

        final ResponseEntity orderValidation = validateOrder( order );
        if ( orderValidation != null ) {
            return orderValidation;
        }

        final Order orderToPersist = new Order();

        // creates a brand new order to ensure status is properly set without
        // having to check for it
        orderToPersist.setOwnerUserName( order.getOwnerUserName() );
        orderToPersist.setRecipe( order.getRecipe() );

        orderService.save( orderToPersist );
        return new ResponseEntity(
                successResponse(
                        "Order id: " + order.getId() + " for " + order.getOwnerUserName() + "  successfully created." ),
                HttpStatus.OK );

    }

    /**
     * REST API method to provide PUT access to the Order model. This is used to
     * update an Order's orderStatus.
     *
     * @param id
     *            the id of the order to update
     * @return ResponseEntity indicating success if the Order could be saved, or
     *         an error if it could not be
     */
    @PutMapping ( BASE_PATH + "/orders/{id}" )
    public ResponseEntity updateOrder ( @PathVariable ( "id" ) final long id ) {
        final Order order = orderService.findById( id );

        final Inventory inventory = inventoryService.getInventory();

        if ( order.getStatus().equals( OrderStatus.ORDERSTARTED ) ) {
            if ( inventory.useIngredients( order.getRecipe() ) ) {
                order.updateStatus();
                orderService.save( order );
                return new ResponseEntity( successResponse( "Order id: " + order.getId() + " for "
                        + order.getOwnerUserName() + "  status successfully set to \"" + order.getStatus() + "\"" ),
                        HttpStatus.OK );
            }
            else {
                return new ResponseEntity( errorResponse( "Unable to fulfill order. Insufficient Inventory." ),
                        HttpStatus.BAD_REQUEST );
            }
        }

        else {
            if ( order.updateStatus() == false ) {
                return new ResponseEntity(
                        errorResponse( "Illegal status update. The order is closed and its status cannot be updated." ),
                        HttpStatus.BAD_REQUEST );
            }

            orderService.save( order );
            return new ResponseEntity( successResponse( "Order id: " + order.getId() + " for "
                    + order.getOwnerUserName() + "  status successfully set to \"" + order.getStatus() + "\"" ),
                    HttpStatus.OK );
        }

    }

    /**
     * helper method to check if an order is legal
     *
     * @param order
     *            the order to check
     * @return error message if order is invalid. Returns null if order is legal
     */
    private ResponseEntity validateOrder ( final Order order ) {
        if ( order.getOwnerUserName() == null || order.getOwnerUserName() == "" ) {
            return new ResponseEntity( errorResponse( "Order username is Invalid" ), HttpStatus.BAD_REQUEST );
        }

        if ( recipeService.findByName( order.getRecipe().getName() ) == null ) {
            return new ResponseEntity( errorResponse( "Recipe associated with this order does not exist" ),
                    HttpStatus.BAD_REQUEST );
        }
        return null;
    }

}
