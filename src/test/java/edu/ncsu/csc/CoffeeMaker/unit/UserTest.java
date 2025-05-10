package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Barista;
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Manager;
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.UserForm;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderStatus;
import edu.ncsu.csc.CoffeeMaker.models.enums.Role;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class UserTest {

    @SuppressWarnings ( "rawtypes" )
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup () {
        userService.deleteAll();
    }

    /**
     * @author iwnowell
     */
    @SuppressWarnings ( "unchecked" )
    @Test
    @Transactional
    public void testCreateUserValid () {
        // create new customer, barista, and manager user
        final UserForm u1 = new UserForm( "iwnowell", "CSC123", Role.ROLE_CUSTOMER, 1 );
        final Customer customer = new Customer( u1 );
        final UserForm u2 = new UserForm( "jwcheek2", "ECE123", Role.ROLE_BARISTA, 1 );
        final Barista barista = new Barista( u2 );
        final UserForm u3 = new UserForm( "dkang8", "MAE123", Role.ROLE_MANAGER, 1 );
        final Manager manager = new Manager( u3 );

        // add each user to the service
        assertEquals( 0, userService.count() );
        userService.save( customer );
        assertEquals( 1, userService.count() );

        assertEquals( 1, userService.count() );
        userService.save( barista );
        assertEquals( 2, userService.count() );

        assertEquals( 2, userService.count() );
        userService.save( manager );
        assertEquals( 3, userService.count() );
    }

    /**
     * @author iwnowell
     */
    @Test
    @Transactional
    public void testCreateUserInvalidRole () {
        // try to create new customer with an invalid role
        final UserForm u1 = new UserForm( "iwnowell", "CSC123", Role.ROLE_MANAGER, 1 );
        try {
            @SuppressWarnings ( "unused" )
            final Customer customer = new Customer( u1 );
        }

        catch ( final IllegalArgumentException ex ) {
            // expected
        }

    }

    /**
     * @author iwnowell
     */
    @Test
    @Transactional
    public void testCreateUserInvalidUsername () {
        // try to create new customer with an invalid username
        final UserForm u1 = new UserForm( null, "CSC123", Role.ROLE_CUSTOMER, 1 );
        try {
            @SuppressWarnings ( "unused" )
            final Customer customer = new Customer( u1 );
        }

        catch ( final IllegalArgumentException ex ) {
            // expected
        }

    }

    /**
     * @author iwnowell
     */
    @SuppressWarnings ( "unchecked" )
    @Test
    @Transactional
    public void testUserToString () {
        final UserForm u1 = new UserForm( "iwnowell", "CSC123", Role.ROLE_CUSTOMER, 1 );
        final Customer customer = new Customer( u1 );

        userService.save( customer );

        final List<User> users = userService.findAll();
        // ensure fields are correct after being retrieved
        assertEquals( customer, users.get( 0 ) );
        assertEquals( "iwnowell", users.get( 0 ).getUsername() );
        assertEquals( "iwnowell", users.get( 0 ).getId() );
        assertEquals( "User [username=" + users.get( 0 ).getUsername() + ", password=" + users.get( 0 ).getPassword()
                + ", roles=" + users.get( 0 ).getRoles().toString() + ", enabled=" + users.get( 0 ).getEnabled() + "]",
                users.get( 0 ).toString() );
    }

    @Test
    @Transactional
    public void testCustomerPlaceOrder () {
        final UserForm u1 = new UserForm( "iwnowell", "CSC123", Role.ROLE_CUSTOMER, 1 );
        final Customer customer = new Customer( u1 );

        final Recipe r1 = createRecipe( "regular coffee", 2, 5 );

        final Order newOrder = customer.orderDrink( r1 );

        // assert customer has a new order and the order has all correct options
        assertEquals( 1, customer.getOrders().size() );
        assertEquals( newOrder, customer.getOrders().get( 0 ) );
        assertEquals( "iwnowell", newOrder.getOwnerUserName() );
        assertEquals( r1, newOrder.getRecipe() );
        assertEquals( OrderStatus.ORDERSTARTED, newOrder.getStatus() );
    }

    @Test
    @Transactional
    public void testBaristaMakeOrder () {
        final UserForm u2 = new UserForm( "jwcheek2", "ECE123", Role.ROLE_BARISTA, 1 );
        final Barista barista = new Barista( u2 );

        final Recipe r1 = createRecipe( "regular coffee", 2, 5 );
        final Order order = new Order();
        order.setRecipe( r1 );
        order.setOwnerUserName( "jwcheek2" );

        barista.makeOrder( order );
        // assert barista has a new order
        assertEquals( 1, barista.getOrders().size() );
        assertEquals( order, barista.getOrders().get( 0 ) );
        assertEquals( "jwcheek2", barista.getOrders().get( 0 ).getOwnerUserName() );
        assertEquals( r1, barista.getOrders().get( 0 ).getRecipe() );
        assertEquals( OrderStatus.ORDERREADYFORPICKUP, barista.getOrders().get( 0 ).getStatus() );

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
