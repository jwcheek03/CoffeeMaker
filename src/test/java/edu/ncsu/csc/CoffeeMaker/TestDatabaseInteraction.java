package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )

public class TestDatabaseInteraction {
    @Autowired
    private RecipeService recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testRecipes () {

        final Recipe r = new Recipe();

        final Ingredient coffee = new Ingredient( "coffee", 2 );
        final Ingredient milk = new Ingredient( "milk", 3 );
        final Ingredient pumpkinSpice = new Ingredient( "pumpkin spice", 1 );
        final Ingredient syrup = new Ingredient( "syrup", 5 );

        r.addIngredient( coffee );
        r.addIngredient( milk );
        r.addIngredient( pumpkinSpice );
        r.addIngredient( syrup );

        r.setPrice( 4 );

        r.setName( "Coffee Recipe" );

        recipeService.save( r );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );

        assertEquals( r.getName(), dbRecipe.getName() );

        final List<Ingredient> dbIngredients = dbRecipe.getIngredients();
        final List<Ingredient> ingredients = r.getIngredients();
        /* Test all of the fields */
        assertEquals( ingredients.get( 0 ).getAmount(), dbIngredients.get( 0 ).getAmount() );
        assertEquals( ingredients.get( 1 ).getAmount(), dbIngredients.get( 1 ).getAmount() );
        assertEquals( ingredients.get( 2 ).getAmount(), dbIngredients.get( 2 ).getAmount() );
        assertEquals( ingredients.get( 3 ).getAmount(), dbIngredients.get( 3 ).getAmount() );
        assertEquals( r.getPrice(), dbRecipe.getPrice() );
        assertEquals( r.getName(), dbRecipe.getName() );

        assertEquals( r.getName(), recipeService.findByName( "Coffee Recipe" ).getName() );

        syrup.setAmount( 12 );

        dbRecipe.setPrice( 15 );
        dbRecipe.addIngredient( syrup );
        recipeService.save( dbRecipe );

        final List<Recipe> dbRecipesFinal = recipeService.findAll();

        assertEquals( 1, dbRecipesFinal.size() );

        final Recipe dbRecipeFinal = dbRecipesFinal.get( 0 );

        final List<Ingredient> dbIngredientsFinal = dbRecipeFinal.getIngredients();

        assertEquals( 1, (int) dbIngredientsFinal.get( 2 ).getAmount() );
        assertEquals( 2, (int) dbIngredientsFinal.get( 0 ).getAmount() );
        assertEquals( 3, (int) dbIngredientsFinal.get( 1 ).getAmount() );
        assertEquals( 15, (int) dbRecipeFinal.getPrice() );
        assertEquals( 12, (int) dbIngredientsFinal.get( 3 ).getAmount() );
        assertEquals( "Coffee Recipe", r.getName() );

    }

    /**
     * @author asantho
     */
    @Test
    @Transactional
    public void testServiceFindAndExists () {
        // Create new recipes and save it
        final Recipe r1 = new Recipe();

        final Ingredient coffee = new Ingredient( "coffee", 5 );
        final Ingredient milk = new Ingredient( "milk", 5 );
        final Ingredient pumpkinSpice = new Ingredient( "pumpkin spice", 5 );
        final Ingredient syrup = new Ingredient( "syrup", 5 );

        r1.addIngredient( coffee );
        r1.addIngredient( milk );
        r1.addIngredient( pumpkinSpice );
        r1.addIngredient( syrup );

        r1.setPrice( 5 );
        r1.setName( "Recipe 1" );

        recipeService.save( r1 );

        final Recipe r2 = new Recipe();

        final Ingredient coffee2 = new Ingredient( "coffee2", 5 );
        final Ingredient milk2 = new Ingredient( "milk2", 5 );
        final Ingredient pumpkinSpice2 = new Ingredient( "pumpkin spice2", 5 );
        final Ingredient syrup2 = new Ingredient( "syrup2", 5 );

        r2.addIngredient( coffee2 );
        r2.addIngredient( milk2 );
        r2.addIngredient( pumpkinSpice2 );
        r2.addIngredient( syrup2 );

        r2.setPrice( 5 );
        r2.setName( "Recipe 2" );

        recipeService.save( r2 );

        // Manually obtain the Recipe's ID
        final Long id1 = r1.getId();
        final Long id2 = null;
        // Check if the findById method returns the correct recipes
        Assertions.assertEquals( r1, recipeService.findById( id1 ) );
        Assertions.assertEquals( null, recipeService.findById( id2 ) );
        // Check if the existsById method returns the correct status
        Assertions.assertTrue( recipeService.existsById( id1 ) );

    }

}
