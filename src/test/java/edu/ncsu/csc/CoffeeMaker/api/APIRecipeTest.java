package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import edu.ncsu.csc.CoffeeMaker.controllers.APIRecipeController;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )

/**
 * Tests the Recipe API functionality and MockMvc uses Spring's testing
 * framework to handle requests to the REST API
 *
 * @author iwnowell
 */
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc                   mvc;

    final Ingredient                  coffee              = new Ingredient( "coffee", 500 );
    final Ingredient                  milk                = new Ingredient( "milk", 500 );
    final Ingredient                  syrup               = new Ingredient( "syrup", 500 );
    final Ingredient                  pumpkinSpice        = new Ingredient( "pumpkin spice", 500 );

    @Autowired
    private WebApplicationContext     context;

    @Autowired
    private RecipeService             service;

    @Autowired
    private final APIRecipeController apiRecipeController = new APIRecipeController();

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     *
     * @throws Exception
     *             for invalid commands
     */
    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        final Recipe r = new Recipe();

        coffee.setAmount( 3 );
        pumpkinSpice.setAmount( 5 );
        milk.setAmount( 4 );
        syrup.setAmount( 8 );

        r.addIngredient( coffee );
        r.addIngredient( pumpkinSpice );
        r.addIngredient( milk );
        r.addIngredient( syrup );
        r.setPrice( 10 );
        r.setName( "Mocha" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

    }

    /**
     *
     * @throws Exception
     *             for Invalid commands
     */
    @Test
    @Transactional
    public void testRecipeAPI () throws Exception {

        service.deleteAll();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );

        coffee.setAmount( 1 );
        pumpkinSpice.setAmount( 10 );
        milk.setAmount( 20 );
        syrup.setAmount( 5 );

        recipe.addIngredient( coffee );
        recipe.addIngredient( pumpkinSpice );
        recipe.addIngredient( milk );
        recipe.addIngredient( syrup );

        recipe.setPrice( 5 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    /**
     *
     * @throws Exception
     *             for Invalid commands
     */
    @Test
    @Transactional
    public void testAddRecipe2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    /**
     *
     * @throws Exception
     *             for Invalid commands
     */
    @Test
    @Transactional
    public void testAddRecipe15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final Recipe r4 = createRecipe( "Hot Chocolate", 75, 0, 2, 1, 2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffeeAmount,
            final Integer milkAmount, final Integer SyrupAmount, final Integer pumpkinSpiceAmount ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );

        coffee.setAmount( coffeeAmount );
        pumpkinSpice.setAmount( pumpkinSpiceAmount );
        milk.setAmount( milkAmount );
        syrup.setAmount( SyrupAmount );

        recipe.addIngredient( coffee );
        recipe.addIngredient( pumpkinSpice );
        recipe.addIngredient( milk );
        recipe.addIngredient( syrup );

        return recipe;
    }

    /**
     *
     * @throws Exception
     *             for Invalid commands
     */
    @Test
    @Transactional
    public void testDeleteRecipe () throws Exception {
        String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        if ( !recipe.contains( "Mocha" ) ) {
            final Recipe r = new Recipe();

            coffee.setAmount( 7 );
            pumpkinSpice.setAmount( 8 );
            milk.setAmount( 6 );
            syrup.setAmount( 8 );

            r.addIngredient( coffee );
            r.addIngredient( pumpkinSpice );
            r.addIngredient( milk );
            r.addIngredient( syrup );

            r.setName( "Mocha" );
            r.setPrice( 5 );

            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );
        }
        // Making sure that the recipe we just instructed the server to create
        // is actually there
        recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        assertTrue( recipe.contains( "Mocha" ) );
        // Delete the recipe
        Assertions.assertEquals( 1, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        service.delete( service.findByName( "Mocha" ) );
        mvc.perform( get( "/api/v1/recipes/Mocha", "Mocha" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests the API functionality
     *
     * @author iwnowell
     *
     * @throws Exception
     *             for Invalid commands
     */
    @Test
    @Transactional
    public void testRecipeAPIController () throws Exception {

        service.deleteAll();
        // Create two new Recipes and use the APIRecipeController to
        // create them
        final Recipe r = new Recipe();
        r.setName( "Latte" );
        r.setPrice( 10 );
        r.addIngredient( coffee );
        apiRecipeController.createRecipe( r );
        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 10 );
        r2.addIngredient( coffee );
        apiRecipeController.createRecipe( r2 );

        // Obtain the list of Recipes using getRecipes() and verify the
        // size.
        final List<Recipe> recipeList = apiRecipeController.getRecipes();
        assertEquals( 2, recipeList.size() );
        apiRecipeController.deleteRecipe( "Mocha" );
    }

}
