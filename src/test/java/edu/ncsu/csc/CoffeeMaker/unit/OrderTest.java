package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class OrderTest {

    @Autowired
    private OrderService  orderService;

    @Autowired
    private RecipeService recipeService;

    @BeforeEach
    public void setup () {
        orderService.deleteAll();
    }

    @Test
    @Transactional
    public void testAddOrder () {
        // need a recipe in the system to create an order
        final Recipe r1 = createRecipe( "regular coffee", 2, 5 );

        recipeService.save( r1 );

        assertEquals( 0, orderService.count(), "No orders have been created so database should be empty" );

        final Order o1 = createOrder( r1, "jeshockl" );
        orderService.save( o1 );

        assertEquals( 1, orderService.count(), "Creating an order should result in an order in the database" );
    }

    @Test
    @Transactional
    public void testAddMultipleOrder () {
        // need recipes in the system to create an order
        final Recipe r1 = createRecipe( "regular coffee", 2, 5 );
        final Recipe r2 = createRecipe( "medium coffee", 3, 8 );

        recipeService.save( r1 );
        recipeService.save( r2 );

        assertEquals( 0, orderService.count(), "No orders have been created so database should be empty" );

        final Order o1 = createOrder( r1, "jeshockl" );
        orderService.save( o1 );

        assertEquals( 1, orderService.count(), "Creating an order should result in an order in the database" );

        final Order o2 = createOrder( r2, "asantho" );
        orderService.save( o2 );

        assertEquals( 2, orderService.count(), "Creating two orders should result in two orders in the database" );
    }

    @Test
    @Transactional
    public void testAddOrderInvalidRecipe () {
        assertEquals( 0, orderService.count(), "No orders have been created so database should be empty" );

        final Order o1 = createOrder( null, "jeshockl" );

        try {
            orderService.save( o1 );
            fail( "Saving an order with a null recipe should result in a ConstraintViolationException" );

        }
        catch ( final ConstraintViolationException cvee ) {
            // expecting an exception to be thrown because an order is trying to
            // be saved with a null recipe

        }

    }

    @Test
    @Transactional
    public void testAddOrderInvalidName () {
        // need a recipe in the system to create an order
        final Recipe r1 = createRecipe( "regular coffee", 2, 5 );

        recipeService.save( r1 );

        assertEquals( 0, orderService.count(), "No orders have been created so database should be empty" );

        final Order o1 = createOrder( r1, "" );

        try {
            orderService.save( o1 );
            fail( "Saving an order with an empty username should result in a ConstraintViolationException" );

        }
        catch ( final ConstraintViolationException cvee ) {
            // expected

        }

    }

    @Test
    @Transactional
    public void testAddMultipleOrdersInvalid () {
        // need a recipe in the system to create an order
        final Recipe r1 = createRecipe( "regular coffee", 2, 5 );
        recipeService.save( r1 );

        assertEquals( 0, orderService.count(), "No orders have been created so database should be empty" );

        final Order o1 = createOrder( r1, "jeshockl" );
        final Order o2 = createOrder( null, "jeshockl" );
        final List<Order> orders = List.of( o1, o2 );

        try {

            orderService.saveAll( orders );
            fail( "Trying to save a collection of orders where one is invalid should result in none being saved" );

        }
        catch ( final ConstraintViolationException cvee ) {
            // expected

        }

    }

    @Test
    @Transactional
    public void testEditOrderStatus () {
        // need a recipe in the system to create an order
        final Recipe r1 = createRecipe( "regular coffee", 2, 5 );

        recipeService.save( r1 );

        final Order o1 = createOrder( r1, "jeshockl" );
        orderService.save( o1 );

        List<Order> orders = orderService.findAll();
        assertEquals( OrderStatus.ORDERSTARTED, orders.get( 0 ).getStatus() );
        orders.get( 0 ).updateStatus();
        orderService.save( o1 );

        orders = orderService.findAll();
        assertEquals( OrderStatus.ORDERREADYFORPICKUP, orders.get( 0 ).getStatus() );

    }

    @Test
    @Transactional
    public void testOrderToString () {
        // need a recipe in the system to create an order
        final Recipe r1 = createRecipe( "regular coffee", 2, 5 );

        recipeService.save( r1 );

        final Order o1 = createOrder( r1, "jeshockl" );
        orderService.save( o1 );

        final List<Order> orders = orderService.findAll();
        // ensure fields are correct after being retrieved
        assertEquals( r1, orders.get( 0 ).getRecipe() );
        assertEquals( "jeshockl", orders.get( 0 ).getOwnerUserName() );
        assertEquals( OrderStatus.ORDERSTARTED, orders.get( 0 ).getStatus() );
        assertEquals( "Order id " + orders.get( 0 ).getId()
                + " for username: jeshockl. Recipe=regular coffee. Ingredients:\n[name=coffee, amount=5]\nstatus=ORDERSTARTED",
                orders.get( 0 ).toString() );
    }

    @SuppressWarnings ( "unlikely-arg-type" )
    @Test
    @Transactional
    public void testOrderEquals () {
        // need a recipe in the system to create an order
        final Recipe r1 = createRecipe( "regular coffee", 2, 5 );

        final Order o1 = createOrder( r1, "jeshockl" );
        o1.updateStatus();
        final Order o2 = createOrder( r1, "jeshockl" );

        assertFalse( o1.equals( null ) );
        assertFalse( o1.equals( o2 ) );
        assertTrue( o1.equals( o1 ) );
        assertFalse( o1.equals( r1 ) );

        o2.updateStatus();

        assertTrue( o1.equals( o2 ) );
        assertTrue( o2.equals( o1 ) );

        assertEquals( o1.hashCode(), o2.hashCode() );

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
