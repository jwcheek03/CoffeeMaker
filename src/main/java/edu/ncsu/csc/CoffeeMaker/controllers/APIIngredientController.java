package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * APIIngredientController Class that is used to work with API endpoints related
 * to Ingredient creation, addition, and deletion
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIIngredientController extends APIController {
    /**
     * IngredientService object, to be autowired in by Spring to allow for
     * manipulating the Ingredient model
     */
    @Autowired
    private IngredientService service;

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService  invService;

    /**
     * REST API method to provide GET access to all ingredients in the system
     *
     * @return JSON representation of all recipes
     */
    @GetMapping ( BASE_PATH + "/ingredients" )
    public List<Ingredient> getIngredients () {
        return service.findAll();
    }

    /**
     * REST API method to provide GET access to a specific ingredient, as
     * indicated by the path variable provided (the name of the ingredient
     * desired)
     *
     * @param id
     *            ingredient id
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/ingredients/{id}" )

    public ResponseEntity getIngredient ( @PathVariable ( "id" ) final long id ) {
        final Ingredient ingredient = service.findById( id );
        return null == ingredient
                ? new ResponseEntity( errorResponse( "No ingredient found with id " + id ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( ingredient, HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Ingredient model. This is
     * used to create a new Ingredient by automatically converting the JSON
     * RequestBody provided to a Ingredient object. Invalid JSON will fail.
     *
     * @param ingredient
     *            The valid Ingredient to be saved.
     * @return ResponseEntity indicating success if the Ingredient could be
     *         saved to the inventory, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/ingredients" )
    public ResponseEntity createIngredient ( @RequestBody final Ingredient ingredient ) {

        if ( ingredient.getName() == null || ingredient.getName() == "" ) {
            return new ResponseEntity( errorResponse( "Ingredient Name is Invalid" ), HttpStatus.BAD_REQUEST );
        }

        if ( ingredient.getAmount() == null || ingredient.getAmount() <= 0 ) {
            return new ResponseEntity( errorResponse( "Ingredient Amount is Invalid" ), HttpStatus.BAD_REQUEST );
        }

        for ( final Ingredient inventoryIngredient : getIngredients() ) {
            if ( ingredient.getName().equals( inventoryIngredient.getName() ) ) {
                return new ResponseEntity( errorResponse( "Ingredient Already Exists" ), HttpStatus.BAD_REQUEST );
            }
        }
        final Inventory i = invService.getInventory();
        i.getIngredients().add( ingredient );
        invService.save( i );
        return new ResponseEntity(
                successResponse( "\"" + ingredient.getName() + "\" Ingredient successfully created" ), HttpStatus.OK );

    }

    /**
     * REST API method to allow deleting an Ingredient from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the ingredient to delete (as a path variable)
     *
     * @param id
     *            The id of the Ingredient to delete
     * @return Success if the ingredient could be deleted; an error if the
     *         ingredient does not exist
     */
    @DeleteMapping ( BASE_PATH + "/ingredients/{id}" )
    public ResponseEntity deleteIngredient ( @PathVariable final long id ) {
        final Ingredient ingredient = service.findById( id );
        if ( null == ingredient ) {
            return new ResponseEntity( errorResponse( "No ingredient found for id " + id ), HttpStatus.NOT_FOUND );
        }
        service.delete( ingredient );

        return new ResponseEntity( successResponse( id + " was deleted successfully" ), HttpStatus.OK );
    }
}
