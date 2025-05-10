package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

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
import edu.ncsu.csc.CoffeeMaker.controllers.APIInventoryController;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

    @Autowired
    private InventoryService             inventoryService;

    @Autowired
    private final APIInventoryController apiInventoryController = new APIInventoryController();

    final Ingredient                     coffee                 = new Ingredient( "coffee", 500 );
    final Ingredient                     milk                   = new Ingredient( "milk", 500 );
    final Ingredient                     pumpkinSpice           = new Ingredient( "pumpkin spice", 500 );
    final Ingredient                     syrup                  = new Ingredient( "syrup", 500 );

    /**
     * Tests for add Ingredients and also sets up initial ingredient array
     */
    @SuppressWarnings ( "unchecked" )
    @BeforeEach
    public void setup () {
        final Inventory ivt = inventoryService.getInventory();

        // Create 4 new Ingredients and assign a value of 500 to each of them
        coffee.setAmount( 500 );
        milk.setAmount( 500 );
        pumpkinSpice.setAmount( 500 );
        syrup.setAmount( 500 );

        final ArrayList<Ingredient> ingList = new ArrayList<Ingredient>();

        ingList.add( coffee );
        ingList.add( milk );
        ingList.add( pumpkinSpice );
        ingList.add( syrup );

        ivt.addIngredients( ingList );

        inventoryService.save( ivt );
    }

    @Test
    @Transactional
    public void testAddInventoryValid () {
        final Inventory i = inventoryService.getInventory();

    }

    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory i = inventoryService.getInventory();

        final Ingredient coffee = new Ingredient( "coffee", 1 );
        final Ingredient milk = new Ingredient( "milk", 20 );
        final Ingredient pumpkinSpice = new Ingredient( "pumpkin spice", 10 );
        final Ingredient syrup = new Ingredient( "syrup", 5 );

        final Recipe recipe = new Recipe();
        recipe.setName( "Pumpkin Spice Latte" );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        recipe.addIngredient( pumpkinSpice );
        recipe.addIngredient( syrup );
        recipe.setPrice( 5 );

        i.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */

        Assertions.assertEquals( 490, (int) i.getIngredientOfIngredientType( "pumpkin spice" ).getAmount() );
        Assertions.assertEquals( 480, (int) i.getIngredientOfIngredientType( "milk" ).getAmount() );
        Assertions.assertEquals( 495, (int) i.getIngredientOfIngredientType( "syrup" ).getAmount() );
        Assertions.assertEquals( 499, (int) i.getIngredientOfIngredientType( "coffee" ).getAmount() );
    }

    /**
     * @author jeshockl
     */
    @Test
    @Transactional
    public void testSetIngredientInvalidInput () {
        final Inventory ivt = inventoryService.getInventory();
        assertEquals( 500, ivt.getIngredientOfIngredientType( "coffee" ).getAmount() );
        assertEquals( 500, ivt.getIngredientOfIngredientType( "milk" ).getAmount() );
        assertEquals( 500, ivt.getIngredientOfIngredientType( "syrup" ).getAmount() );
        assertEquals( 500, ivt.getIngredientOfIngredientType( "pumpkin spice" ).getAmount() );

        coffee.setAmount( -1 );
        milk.setAmount( -1 );
        pumpkinSpice.setAmount( -1 );
        syrup.setAmount( -1 );

        final ArrayList<Ingredient> ingList = new ArrayList<Ingredient>();

        ingList.add( coffee );
        ingList.add( milk );
        ingList.add( pumpkinSpice );
        ingList.add( syrup );

        ivt.addIngredients( ingList );

        assertEquals( 500, ivt.getIngredientOfIngredientType( "coffee" ).getAmount() );
        assertEquals( 500, ivt.getIngredientOfIngredientType( "milk" ).getAmount() );
        assertEquals( 500, ivt.getIngredientOfIngredientType( "syrup" ).getAmount() );
        assertEquals( 500, ivt.getIngredientOfIngredientType( "pumpkin spice" ).getAmount() );
    }

    /**
     * @author jeshockl and iwnowell
     */
    @Test
    @Transactional
    public void testToString () {
        final Inventory ivt = inventoryService.getInventory();

        coffee.setAmount( 90 );
        milk.setAmount( 80 );
        pumpkinSpice.setAmount( 100 );
        syrup.setAmount( 70 );

        ivt.setIngredient( coffee );
        ivt.setIngredient( milk );
        ivt.setIngredient( pumpkinSpice );
        ivt.setIngredient( syrup );

        assertEquals( 90, ivt.getIngredientOfIngredientType( "coffee" ).getAmount() );
        assertEquals( 80, ivt.getIngredientOfIngredientType( "milk" ).getAmount() );
        assertEquals( 70, ivt.getIngredientOfIngredientType( "syrup" ).getAmount() );
        assertEquals( 100, ivt.getIngredientOfIngredientType( "pumpkin spice" ).getAmount() );

        final String inventoryToString = ivt.toString();
        assertEquals(
                "Inventory: [[name=coffee, amount=90], [name=milk, amount=80], [name=pumpkin spice, amount=100], [name=syrup, amount=70]]",
                inventoryToString );
        apiInventoryController.getInventory(); // Make sure getInventory() can
                                               // still be called

    }

    /**
     * @author iwnowell
     */
    @Test
    @Transactional
    public void testEnoughIngredients () {
        final Inventory ivt = inventoryService.getInventory();

        // set ingredients to 0
        coffee.setAmount( 0 );
        milk.setAmount( 0 );
        pumpkinSpice.setAmount( 0 );
        syrup.setAmount( 0 );

        ivt.setIngredient( coffee );
        ivt.setIngredient( milk );
        ivt.setIngredient( pumpkinSpice );
        ivt.setIngredient( syrup );

        // try to make a recipe when there are not enough ingredients
        final Recipe recipe = new Recipe();

        coffee.setAmount( 10 );
        milk.setAmount( 10 );
        pumpkinSpice.setAmount( 10 );
        syrup.setAmount( 10 );

        recipe.setName( "Mocha" );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        recipe.addIngredient( pumpkinSpice );
        recipe.addIngredient( syrup );

        recipe.setPrice( 10 );

        assertTrue( !ivt.enoughIngredients( recipe ) );

    }

    /**
     * @author jeshockl
     */
    @Test
    @Transactional
    public void testCheckIngredientValidInput () {
        final Inventory ivt = inventoryService.getInventory();

        Assertions.assertEquals( 500, ivt.checkIngredient( "500" ) );

    }

    /**
     * @author jeshockl
     */
    @Test
    @Transactional
    public void testCheckIngredientInvalidInput () {
        final Inventory ivt = inventoryService.getInventory();
        try {
            ivt.checkIngredient( "abc" );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getIngredientOfIngredientType( "coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- chocolate" );
            assertEquals( "Units of ingredient must be a positive integer", iae.getMessage() );
        }

        try {
            ivt.checkIngredient( "-50" );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getIngredientOfIngredientType( "coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
        }

    }

    @Test
    @Transactional
    public void testAddInventoryInvalidInput () {
        final Inventory ivt = inventoryService.getInventory();

        try {

            coffee.setAmount( -5 );
            milk.setAmount( 3 );
            pumpkinSpice.setAmount( 7 );
            syrup.setAmount( 2 );

            final ArrayList<Ingredient> ingList = new ArrayList<Ingredient>();

            ingList.add( coffee );
            ingList.add( milk );
            ingList.add( pumpkinSpice );
            ingList.add( syrup );

            ivt.addIngredients( ingList );

        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getIngredientOfIngredientType( "coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.getIngredientOfIngredientType( "milk" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.getIngredientOfIngredientType( "pumpkin spice" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.getIngredientOfIngredientType( "syrup" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );
        }
    }

}
