package edu.ncsu.csc.CoffeeMaker.unit;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RecipeTest {

    @Autowired
    private RecipeService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testAddRecipe () {

        final Ingredient coffee = new Ingredient( "coffee", 1 );
        final Ingredient milk = new Ingredient( "milk", 2 );
        final Ingredient pumpkinSpice = new Ingredient( "pumpkin spice", 3 );

        final Recipe r1 = new Recipe();
        r1.setName( "Pumpkin Spice Latte" );
        r1.setPrice( 1 );

        r1.addIngredient( coffee );
        r1.addIngredient( milk );
        r1.addIngredient( pumpkinSpice );

        service.save( r1 );

        final Ingredient milk2 = new Ingredient( "milk", 3 );

        final Recipe r2 = new Recipe();
        r2.setName( "Milk Supreme" );
        r2.setPrice( 2 );

        r2.addIngredient( milk2 );

        service.save( r2 );

        final List<Recipe> recipes = service.findAll();
        Assertions.assertEquals( 2, recipes.size(),
                "Creating two recipes should result in two recipes in the database" );

        Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe should match the created one" );
        Assertions.assertEquals( r2, recipes.get( 1 ), "The retrieved recipe should match the created one" );
    }

    @Test
    @Transactional
    public void testNoRecipes () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Ingredient coffee = new Ingredient( "coffee", 1 );
        final Ingredient milk = new Ingredient( "milk", 2 );
        final Ingredient pumpkinSpice = new Ingredient( "pumpkin spice", 3 );
        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        r1.setPrice( 12 );
        r1.addIngredient( coffee );
        r1.addIngredient( milk );
        r1.addIngredient( pumpkinSpice );

        final Ingredient coffee2 = new Ingredient( "coffee", -1 );
        final Ingredient milk2 = new Ingredient( "milk", 0 );
        final Ingredient pumpkinSpice2 = new Ingredient( "pumpkin spice", 0 );
        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( coffee2 );
        r2.addIngredient( milk2 );
        r2.addIngredient( pumpkinSpice2 );

        final List<Recipe> recipes = List.of( r1, r2 );

        try {
            service.saveAll( recipes );
            Assertions.assertEquals( 0, service.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof ConstraintViolationException );
        }

    }

    @Test
    @Transactional
    public void testAddRecipe1 () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );

    }

    /* Test2 is done via the API for different validation */

    @Test
    @Transactional
    public void testAddRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, -50, 3, 1, 1, 0 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative price" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, -3, 1, 1, 2 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of coffee" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, -1, 1, 2 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of milk" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe6 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, -1, 2 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of sugar" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe7 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, -2 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of chocolate" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe13 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(),
                "Creating two recipes should result in two recipes in the database" );

    }

    @Test
    @Transactional
    public void testAddRecipe14 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "`service.deleteAll()` should remove everything" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.delete( r1 );

        Assertions.assertEquals( 2, service.count(), "There should be 2 recipes in the database" );

        service.delete( r2 );

        Assertions.assertEquals( 1, service.count(), "There should be 1 recipe in the database" );

        service.delete( r3 );

        Assertions.assertEquals( 0, service.count(), "There should be 0 recipes in the database" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        try {
            service.deleteAll();

            Assertions.assertNull( service.findByName( "Coffee" ),
                    "A recipe was able to be deleted without ever being created" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }
    }

    @Test
    @Transactional
    public void testEditRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        r1.setPrice( 70 );

        service.save( r1 );

        final Recipe retrieved = service.findByName( "Coffee" );

        final List<Ingredient> ingredients = retrieved.getIngredients();

        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        Assertions.assertEquals( 3, (int) ingredients.get( 0 ).getAmount() );
        Assertions.assertEquals( 1, (int) ingredients.get( 1 ).getAmount() );
        Assertions.assertEquals( 1, (int) ingredients.get( 2 ).getAmount() );
        Assertions.assertEquals( 0, (int) ingredients.get( 3 ).getAmount() );

        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    /**
     * @author iwnowell
     */
    @Test
    @Transactional
    public void testUpdateRecipe () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        r1.setPrice( 70 );
        service.save( r1 );

        final Recipe r2 = createRecipe( "Mocha", 40, 2, 2, 2, 1 );
        service.save( r2 );
        r2.setPrice( 60 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.findAll().size(), "There should be 2 Recipes in the CoffeeMaker" );
        final Recipe retrieved = service.findByName( "Mocha" );

        final List<Ingredient> ingredients = retrieved.getIngredients();

        Assertions.assertEquals( 60, (int) retrieved.getPrice() );
        Assertions.assertEquals( 2, (int) ingredients.get( 0 ).getAmount() );
        Assertions.assertEquals( 2, (int) ingredients.get( 1 ).getAmount() );
        Assertions.assertEquals( 2, (int) ingredients.get( 2 ).getAmount() );
        Assertions.assertEquals( 1, (int) ingredients.get( 3 ).getAmount() );

        // now both recipes will be equal except for their names
        r2.updateRecipe( r1 );
        service.save( r2 );
        final Recipe retrieved2 = service.findByName( "Mocha" );

        final List<Ingredient> ingredients2 = retrieved2.getIngredients();

        Assertions.assertTrue( !r1.equals( retrieved2 ) );

        Assertions.assertEquals( 70, (int) retrieved2.getPrice() );
        Assertions.assertEquals( 3, (int) ingredients2.get( 0 ).getAmount() );
        Assertions.assertEquals( 1, (int) ingredients2.get( 1 ).getAmount() );
        Assertions.assertEquals( 1, (int) ingredients2.get( 2 ).getAmount() );
        Assertions.assertEquals( 0, (int) ingredients2.get( 3 ).getAmount() );

        Assertions.assertEquals( 2, service.count(), "Updating a recipe shouldn't duplicate it" );

    }

    /**
     * @author asantho
     */
    @Test
    @Transactional
    public void testRecipeToString () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Recipe 1", 10, 5, 5, 5, 3 );
        service.save( r1 );
        r1.setPrice( 10 );
        service.save( r1 );

        final Recipe r2 = createRecipe( "Recipe 2", 20, 4, 4, 4, 3 );
        service.save( r2 );
        r2.setPrice( 20 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.findAll().size(), "There should be 2 Recipes in the CoffeeMaker" );

        // Make sure the attributes are correct once retrieved
        final Recipe retrieved = service.findByName( "Recipe 1" );

        final List<Ingredient> ingredients = retrieved.getIngredients();

        Assertions.assertEquals( 10, (int) retrieved.getPrice() );
        Assertions.assertEquals( 5, (int) ingredients.get( 0 ).getAmount() );
        Assertions.assertEquals( 5, (int) ingredients.get( 1 ).getAmount() );
        Assertions.assertEquals( 5, (int) ingredients.get( 2 ).getAmount() );
        Assertions.assertEquals( 3, (int) ingredients.get( 3 ).getAmount() );

        Assertions.assertEquals(
                "Recipe 1. Ingredients:\n[name=coffee, amount=5]\n[name=milk, amount=5]\n[name=syrup, amount=5]\n[name=pumpkin spice, amount=3]\n",
                retrieved.toString() );

    }

    /**
     * @author asantho
     */
    @SuppressWarnings ( "unlikely-arg-type" )
    @Test
    @Transactional
    public void testRecipeEquals () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Recipe 1", 10, 5, 5, 5, 3 );
        service.save( r1 );
        r1.setPrice( 10 );
        service.save( r1 );

        final Recipe r2 = createRecipe( null, 20, 4, 4, 4, 3 );
        service.save( r2 );
        r2.setPrice( 20 );
        service.save( r2 );

        final Recipe r3 = createRecipe( null, 20, 4, 4, 4, 3 );
        service.save( r3 );
        r2.setPrice( 20 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.findAll().size(), "There should be 3 Recipes in the CoffeeMaker" );

        // Make sure the attributes are correct once retrieved
        final Recipe retrieved = service.findByName( "Recipe 1" );

        final List<Ingredient> ingredients = retrieved.getIngredients();

        Assertions.assertEquals( 10, (int) retrieved.getPrice() );
        Assertions.assertEquals( 5, (int) ingredients.get( 0 ).getAmount() );
        Assertions.assertEquals( 5, (int) ingredients.get( 1 ).getAmount() );
        Assertions.assertEquals( 5, (int) ingredients.get( 2 ).getAmount() );
        Assertions.assertEquals( 3, (int) ingredients.get( 3 ).getAmount() );

        // Checking false assert for equals with a null object
        Assertions.assertFalse( retrieved.equals( null ) );

        // Checking false assert for equals with a non comparable object
        Assertions.assertFalse( retrieved.equals( "Test" ) );

        // Checking false assert for equals for a null name with an actual name
        Assertions.assertFalse( r2.equals( retrieved ) );

        // Checking true assert
        Assertions.assertTrue( r2.equals( r3 ) );
    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffeeAmount,
            final Integer milkAmount, final Integer syrupAmount, final Integer pumpkin_spiceAmount ) {
        final Recipe recipe = new Recipe();

        final Ingredient coffee = new Ingredient( "coffee", coffeeAmount );
        final Ingredient milk = new Ingredient( "milk", milkAmount );
        final Ingredient pumpkinSpice = new Ingredient( "pumpkin spice", pumpkin_spiceAmount );
        final Ingredient syrup = new Ingredient( "syrup", syrupAmount );

        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        recipe.addIngredient( syrup );
        recipe.addIngredient( pumpkinSpice );

        return recipe;
    }

}
