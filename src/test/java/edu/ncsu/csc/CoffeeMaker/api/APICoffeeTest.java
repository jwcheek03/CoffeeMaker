package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

    @Autowired
    private MockMvc          mvc;

    @Autowired
    private RecipeService    service;

    @Autowired
    private InventoryService iService;

    final Ingredient         coffee       = new Ingredient( "coffee", 500 );
    final Ingredient         milk         = new Ingredient( "milk", 500 );
    final Ingredient         pumpkinSpice = new Ingredient( "pumpkin spice", 500 );

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
        iService.deleteAll();

        final Inventory ivt = iService.getInventory();

        coffee.setAmount( 500 );
        milk.setAmount( 500 );
        pumpkinSpice.setAmount( 500 );

        final List<Ingredient> testlist = new ArrayList<Ingredient>();
        testlist.add( coffee );
        testlist.add( milk );
        testlist.add( pumpkinSpice );

        ivt.addIngredients( testlist );

        iService.save( ivt );
        coffee.setAmount( 2 );
        milk.setAmount( 2 );
        pumpkinSpice.setAmount( 3 );

        final Recipe recipe = new Recipe();
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        recipe.addIngredient( pumpkinSpice );
        recipe.setName( "Coffee" );
        recipe.setPrice( 50 );

        service.save( recipe );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "BARISTA", "CUSTOMER", "MANAGER" } )
    public void testPurchaseBeverage1 () throws Exception {

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 60 ) ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.message" ).value( 10 ) );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "BARISTA", "CUSTOMER", "MANAGER" } )
    public void testPurchaseBeverage2 () throws Exception {
        /* Insufficient amount paid */

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 40 ) ) )
                .andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "BARISTA", "CUSTOMER", "MANAGER" } )
    public void testPurchaseBeverage3 () throws Exception {
        /* Insufficient inventory */

        final Inventory ivt = iService.getInventory();

        coffee.setAmount( 1 );
        ivt.setIngredient( coffee );
        iService.save( ivt );

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 50 ) ) )
                .andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough inventory" ) );

    }

}
