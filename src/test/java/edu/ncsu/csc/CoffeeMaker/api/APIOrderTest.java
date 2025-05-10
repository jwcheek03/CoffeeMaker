package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

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
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests the Order API functionality and MockMvc uses Spring's testing framework
 * to handle requests to the REST API.
 *
 * @author John Shockley
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIOrderTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OrderService          orderService;

    @Autowired
    private RecipeService         recipeService;

    @Autowired
    private InventoryService      inventoryService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        orderService.deleteAll();
        recipeService.deleteAll();
    }

    @Test
    @Transactional
    public void createOrderTest () throws Exception {
        // need a recipe in the system first
        final Recipe r1 = createRecipe( "regular coffee", 1, 2 );

        recipeService.save( r1 );
        final Recipe databaseR1 = recipeService.findByName( r1.getName() );

        final Order o1 = createOrder( databaseR1, "jeshockl" );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ) ).andExpect( status().isOk() );

        assertEquals( 1, orderService.count() );
    }

    @Test
    @Transactional
    public void createOrderInvalidNameTest () throws Exception {
        // need a recipe in the system first.
        final Recipe r1 = createRecipe( "regular coffee", 1, 2 );

        recipeService.save( r1 );
        final Recipe databaseR1 = recipeService.findByName( r1.getName() );

        final Order o1 = createOrder( databaseR1, "" );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ) ).andExpect( status().isBadRequest() );

        final Order o2 = createOrder( databaseR1, null );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o2 ) ) ).andExpect( status().isBadRequest() );

        assertEquals( 0, orderService.count() );
    }

    @Test
    @Transactional
    public void createOrderInvalidRecipeTest () throws Exception {
        final Order o1 = createOrder( null, "jeshockl" );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ) ).andExpect( status().isBadRequest() );

        assertEquals( 0, orderService.count() );
    }

    @Test
    @Transactional
    public void getOrderTest () throws Exception {
        // need a recipe in the system first
        final Recipe r1 = createRecipe( "regular coffee", 1, 2 );

        recipeService.save( r1 );
        final Recipe databaseR1 = recipeService.findByName( r1.getName() );

        final Order o1 = createOrder( databaseR1, "jeshockl" );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ) ).andExpect( status().isOk() );

        final List<Order> orders = orderService.findAll();

        final String order = mvc.perform( get( "/api/v1/orders/" + orders.get( 0 ).getId() ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertTrue( order.contains( "regular coffee" ) );
        assertTrue( order.contains( "jeshockl" ) );

    }

    @Test
    @Transactional
    public void getAllOrdersTest () throws Exception {
        // need multiple recipes in system first.
        final Recipe r1 = createRecipe( "regular coffee", 1, 2 );
        final Recipe r2 = createRecipe( "medium coffee", 2, 4 );

        recipeService.save( r1 );
        recipeService.save( r2 );
        final Recipe databaseR1 = recipeService.findByName( r1.getName() );
        final Recipe databaseR2 = recipeService.findByName( r2.getName() );

        final Order o1 = createOrder( databaseR1, "jeshockl" );
        final Order o2 = createOrder( databaseR2, "asantho" );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o2 ) ) ).andExpect( status().isOk() );

        final String order = mvc.perform( get( "/api/v1/orders" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        assertTrue( order.contains( "regular coffee" ) );
        assertTrue( order.contains( "jeshockl" ) );

        assertTrue( order.contains( "medium coffee" ) );
        assertTrue( order.contains( "asantho" ) );

    }

    @Test
    @Transactional
    public void getOrderNoneThereTest () throws Exception {
        assertEquals( 0, orderService.count() );

        mvc.perform( get( "/api/v1/orders/123" ) ).andDo( print() ).andExpect( status().isNotFound() ).andReturn()
                .getResponse().getContentAsString();

    }

    @Test
    @Transactional
    public void updateOrderTest () throws Exception {
        // since updating, will have to use inventory so we need an ingredient
        // in system
        final Ingredient coffee = new Ingredient( "coffee", 1 );

        // need to have an inventory to ensure ingredient amounts are deducted.
        final Inventory inventory = inventoryService.getInventory();
        inventory.addIngredients( List.of( coffee ) );
        inventoryService.save( inventory );

        // makes sure there is a coffee ingredient available
        assertEquals( 1, inventoryService.getInventory().getIngredientOfIngredientType( "coffee" ).getAmount() );

        // can now add the recipe in system to create order
        final Recipe r1 = createRecipe( "regular coffee", 2, 1 );

        recipeService.save( r1 );

        final Recipe databaseR1 = recipeService.findByName( r1.getName() );
        final Order o1 = createOrder( databaseR1, "jeshockl" );

        // create the order
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ) ).andExpect( status().isOk() );

        final List<Order> orders = orderService.findAll();
        String order = mvc.perform( get( "/api/v1/orders/" + orders.get( 0 ).getId() ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        // ensure proper status
        assertTrue( order.contains( "ORDERSTARTED" ) );

        // perform update for status
        mvc.perform( put( "/api/v1/orders/" + orders.get( 0 ).getId() ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        order = mvc.perform( get( "/api/v1/orders/" + orders.get( 0 ).getId() ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        // ensure status properly changed and ingredient was used.
        assertTrue( order.contains( "ORDERREADYFORPICKUP" ) );
        assertEquals( 0, inventoryService.getInventory().getIngredientOfIngredientType( "coffee" ).getAmount() );

        // perform update for status
        mvc.perform( put( "/api/v1/orders/" + orders.get( 0 ).getId() ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        order = mvc.perform( get( "/api/v1/orders/" + orders.get( 0 ).getId() ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        // ensure status properly changed
        assertTrue( order.contains( "ORDERCLOSED" ) );

        // perform update for status and ensure it cannot move forward. An order
        // that has a status ORDERCLOSED stays closed
        mvc.perform( put( "/api/v1/orders/" + orders.get( 0 ).getId() ) ).andDo( print() )
                .andExpect( status().isBadRequest() ).andReturn().getResponse().getContentAsString();

    }

    @Test
    @Transactional
    public void updateOrderNoIngredientsTest () throws Exception {
        // since updating, will have to use inventory so we need an ingredient
        // in system
        final Ingredient coffee = new Ingredient( "coffee", 1 );

        // need to have an inventory to ensure ingredient amounts are deducted.
        final Inventory inventory = inventoryService.getInventory();
        inventory.addIngredients( List.of( coffee ) );
        inventoryService.save( inventory );

        // makes sure there is a coffee ingredient available
        assertEquals( 1, inventoryService.getInventory().getIngredientOfIngredientType( "coffee" ).getAmount() );

        // can now add the recipe in system to create order
        final Recipe r1 = createRecipe( "medium coffee", 2, 3 );

        recipeService.save( r1 );

        final Recipe databaseR1 = recipeService.findByName( r1.getName() );
        final Order o1 = createOrder( databaseR1, "jeshockl" );

        // create the order
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ) ).andExpect( status().isOk() );

        final List<Order> orders = orderService.findAll();
        String order = mvc.perform( get( "/api/v1/orders/" + orders.get( 0 ).getId() ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        // ensure proper status
        assertTrue( order.contains( "ORDERSTARTED" ) );

        // perform update for status
        mvc.perform( put( "/api/v1/orders/" + orders.get( 0 ).getId() ) ).andDo( print() )
                .andExpect( status().isBadRequest() ).andReturn().getResponse().getContentAsString();
        order = mvc.perform( get( "/api/v1/orders/" + orders.get( 0 ).getId() ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // ensure status did not change and ingredient was not used.
        assertTrue( order.contains( "ORDERSTARTED" ) );
        assertEquals( 1, inventoryService.getInventory().getIngredientOfIngredientType( "coffee" ).getAmount() );

    }

    /**
     * helper method that constructs an order
     *
     * @param recipe
     *            the recipe for this order
     * @param username
     *            the username associated with this order
     * @return the newly created order
     */
    private Order createOrder ( final Recipe recipe, final String username ) {
        final Order order = new Order();

        order.setRecipe( recipe );
        order.setOwnerUserName( username );

        return order;
    }

    /**
     * helper method that constructs a basic recipe
     *
     * @param name
     *            the name of the recipe
     * @param price
     *            the price of the recipe
     * @param coffeeAmount
     *            the amount of coffee for this recipe
     * @return the newly created recipe
     */
    private Recipe createRecipe ( final String name, final Integer price, final Integer coffeeAmount ) {
        final Recipe recipe = new Recipe();

        final Ingredient coffee = new Ingredient( "coffee", coffeeAmount );

        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( coffee );

        return recipe;
    }

}
