package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class IngredientTest {

    // @Autowired
    // private IngredientService ingredientService;

    /**
     * This tests the constructor for an Ingredient along with the
     * IngredientType and amount
     *
     * @author asantho
     */
    @Test
    @Transactional
    public void testIngredientConstructor () {

        // Create 3 new Ingredients and assign a value of 500 to each of them
        final Ingredient coffee = new Ingredient( "coffee", 500 );
        final Ingredient milk = new Ingredient( "milk", 500 );
        final Ingredient pumpkinSpice = new Ingredient( "pumpkin spice", 500 );

        // Check that the getName() method returns the correct
        // IngredientType
        assertEquals( coffee.getName(), "coffee" );
        assertEquals( milk.getName(), "milk" );
        assertEquals( pumpkinSpice.getName(), "pumpkin spice" );

        // Check that the amounts match up for each Ingredient
        assertEquals( coffee.getAmount(), 500 );
        assertEquals( milk.getAmount(), 500 );
        assertEquals( pumpkinSpice.getAmount(), 500 );

    }

    /**
     * This tests the amount changing functionality in the Ingredient Class
     *
     * @author asantho
     */
    @Test
    @Transactional
    public void testIngredientChangeAmount () {

        // Create 3 new Ingredients and assign a value of 500 to each of them
        final Ingredient coffee = new Ingredient( "coffee", 500 );
        final Ingredient milk = new Ingredient( "milk", 500 );
        final Ingredient pumpkinSpice = new Ingredient( "pumpkin spice", 500 );

        // Check that the amounts match up for each Ingredient
        assertEquals( coffee.getAmount(), 500 );
        assertEquals( milk.getAmount(), 500 );
        assertEquals( pumpkinSpice.getAmount(), 500 );

        // Change the amount of the Ingredient using setAmount()
        coffee.setAmount( 250 );
        milk.setAmount( 250 );
        pumpkinSpice.setAmount( 250 );

        // Check that the amounts match up for each Ingredient
        assertEquals( coffee.getAmount(), 250 );
        assertEquals( milk.getAmount(), 250 );
        assertEquals( pumpkinSpice.getAmount(), 250 );

    }

    /**
     * This test covers the setIngredient Functionality and tests for validity
     * of Ingredient types once they're changed.
     *
     * @author asantho
     */
    @Test
    @Transactional
    public void testIngredientAlternates () {

        // Create 3 new Ingredients and assign a value of 500 to each of them
        final Ingredient coffee = new Ingredient( "coffee", 500 );
        final Ingredient milk = new Ingredient( "milk", 500 );
        final Ingredient pumpkinSpice = new Ingredient( "pumpkin spice", 500 );

        // Check that the amounts match up for each Ingredient
        assertEquals( coffee.getAmount(), 500 );
        assertEquals( milk.getAmount(), 500 );
        assertEquals( pumpkinSpice.getAmount(), 500 );

        // Change the Ingredient Type for all the Ingredients
        coffee.setName( "coffee" );
        milk.setName( "milk" );
        pumpkinSpice.setName( "pumpkin spice" );

        // Now make sure that the Ingredient Type that has been changed is
        // reflected through getName()
        assertEquals( coffee.getName(), "coffee" );
        assertEquals( milk.getName(), "milk" );
        assertEquals( pumpkinSpice.getName(), "pumpkin spice" );

        // Utilize the toString() method to verify the accuracy of each
        // Ingredient
        assertEquals( "[name=coffee, amount=500]", coffee.toString() );
        assertEquals( "[name=milk, amount=500]", milk.toString() );
        assertEquals( "[name=pumpkin spice, amount=500]", pumpkinSpice.toString() );

    }

}
