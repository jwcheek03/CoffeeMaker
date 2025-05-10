package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.controllers.APIIngredientController;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIIngredientTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc                       mvc;

    @Autowired
    private WebApplicationContext         context;

    @Autowired
    private IngredientService             service;

    @Autowired
    private InventoryService              invService;

    @Autowired
    private final APIIngredientController apiIngredientController = new APIIngredientController();

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    public void ensureIngredient () throws Exception {
        service.deleteAll();

        // Create a new Ingredient coffee and post it and check the status
        final Ingredient coffee = new Ingredient( "coffee", 500 );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) ).andExpect( status().isOk() );
    }

    @Test
    @Transactional
    public void testIngredientAPI () throws Exception {

        service.deleteAll();

        // Create a new Ingredient coffee and check the count in the service
        final Ingredient coffee = new Ingredient( "coffee", 500 );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) );
        // Make sure there is only 1 Ingredient present
        Assertions.assertEquals( 1, (int) service.count() );
    }

    /**
     * Tests creating an ingredient with a full database
     *
     * @author iwnowell
     */
    @Test
    @Transactional
    public void testCreateIngredientInvalid () throws Exception {

        service.deleteAll();

        // Create 10 new ingredients (the max allowed) and add them to DB
        final Ingredient coffee = new Ingredient( "coffee", 500 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) );
        final Ingredient milk = new Ingredient( "milk", 500 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( milk ) ) );
        final Ingredient caramel = new Ingredient( "caramel", 500 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( caramel ) ) );
        final Ingredient chocolate = new Ingredient( "chocolate", 500 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( chocolate ) ) );
        final Ingredient vanilla = new Ingredient( "vanilla", 500 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vanilla ) ) );

        final Ingredient lavender = new Ingredient( "lavender", 500 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( lavender ) ) );
        final Ingredient hazelnut = new Ingredient( "hazelnut", 500 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hazelnut ) ) );
        final Ingredient matcha = new Ingredient( "matcha", 500 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( matcha ) ) );
        final Ingredient sugar = new Ingredient( "sugar", 500 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sugar ) ) );
        final Ingredient decaf = new Ingredient( "decaf", 500 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( decaf ) ) );

    }

    @Test
    @Transactional
    public void testAddIngredients () throws Exception {

        service.deleteAll();

        // Create two new Ingredients coffee and milk and post them
        final Ingredient coffee = new Ingredient( "coffee", 500 );
        final Ingredient milk = new Ingredient( "milk", 500 );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( milk ) ) );

        // Use findAll() and check for the size that the service returns
        Assertions.assertEquals( 2, service.findAll().size() );
    }

    @Test
    @Transactional
    public void testDeleteIngredients () throws Exception {

        service.deleteAll();

        // Create two new Ingredients coffee and milk and post them
        final Ingredient coffee = new Ingredient( "coffee", 500 );
        apiIngredientController.createIngredient( coffee );
        final Ingredient milk = new Ingredient( "milk", 500 );
        apiIngredientController.createIngredient( milk );

        service.save( milk );
        service.save( coffee );
        // Obtain the list of Ingredients using getIngredients() and verify the
        // size.

        final List<Ingredient> ingredientsList = invService.getInventory().getIngredients();
        assertEquals( 2, ingredientsList.size() );

        apiIngredientController.deleteIngredient( coffee.getId() );
        apiIngredientController.getIngredient( coffee.getId() );
    }

    @Test
    @Transactional
    public void testIngredientAPIController () throws Exception {

        service.deleteAll();
        // Create two new Ingredients and use the APIIngredientController to
        // create them
        final Ingredient coffee = new Ingredient( "coffee", 500 );
        apiIngredientController.createIngredient( coffee );
        final Ingredient milk = new Ingredient( "milk", 500 );
        apiIngredientController.createIngredient( milk );

        // Obtain the list of Ingredients using getIngredients() and verify the
        // size.
        final List<Ingredient> ingredientsList = apiIngredientController.getIngredients();
        assertEquals( 2, ingredientsList.size() );
    }

}
