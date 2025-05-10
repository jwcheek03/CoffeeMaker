package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for the URL mappings for CoffeeMaker. The controller returns
 * the approprate HTML page in the /src/main/resources/templates folder. For a
 * larger application, this should be split across multiple controllers.
 *
 * @author Kai Presler-Marshall
 */
@Controller
public class MappingController {

    /**
     * On a GET request to /index, the IndexController will return
     * /src/main/resources/templates/index.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/loginCustom" } )
    public String index ( final Model model ) {
        return "loginCustom";
    }

    @GetMapping ( { "/login" } )
    public String login ( final Model model ) {
        return "login";
    }

    /**
     * On a GET request to /recipe, the RecipeController will return
     * /src/main/resources/templates/recipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/recipe", "/recipe.html" } )
    public String addRecipePage ( final Model model ) {
        return "recipe";
    }

    @GetMapping ( { "/home" } )
    public String home ( final Model model ) {
        return "index";
    }

    @GetMapping ( { "/register" } )
    public String register ( final Model model ) {
        return "register";
    }

    /**
     * On a GET request to /ingredient, the IngredientController will return
     * /src/main/resources/templates/ingredient.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/ingredient", "/ingredient.html" } )
    public String addIngredientPage ( final Model model ) {
        return "ingredient";
    }

    /**
     * On a GET request to /deleterecipe, the DeleteRecipeController will return
     * /src/main/resources/templates/deleterecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/deleterecipe", "/deleterecipe.html" } )
    public String deleteRecipeForm ( final Model model ) {
        return "deleterecipe";
    }

    /**
     * On a GET request to /editrecipe, the EditRecipeController will return
     * /src/main/resources/templates/editrecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/editrecipe", "/editrecipe.html" } )
    public String editRecipeForm ( final Model model ) {
        return "editrecipe";
    }

    /**
     * Handles a GET request for inventory. The GET request provides a view to
     * the client that includes the list of the current ingredients in the
     * inventory and a form where the client can enter more ingredients to add
     * to the inventory.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/inventory", "/inventory.html" } )
    public String inventoryForm ( final Model model ) {
        return "inventory";
    }

    /**
     * On a GET request to /makecoffee, the MakeCoffeeController will return
     * /src/main/resources/templates/makecoffee.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/makecoffee", "/makecoffee.html" } )
    public String makeCoffeeForm ( final Model model ) {
        return "makecoffee";
    }

    @GetMapping ( { "/modal", "/modal.html" } )
    public String modalPage ( @RequestParam final String username, final Model model ) {
        model.addAttribute( "username", username );
        return "modal";
    }

    /**
     * On a GET request to /order, the OrderController will return
     * /src/main/resources/templates/order.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/order", "/order.html" } )
    public String makeOrderForm ( final Model model ) {
        return "order";
    }

    /**
     *
     * On a GET request to /order, the OrderController will return
     * /src/main/resources/templates/order.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/orderPickup", "/orderPickup.html" } )
    public String orderPickUpForm ( final Model model ) {
        return "orderPickup";
    }

    /**
     * On a GET request to /orderhistory, the OrderController will return
     * /src/main/resources/templates/orderhistory.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/orderhistory", "/orderhistory.html" } )
    public String viewOrderHistoryForm ( final Model model ) {
        return "orderhistory";
    }

    /**
     * On a GET request to /order_picup, the OrderController will return
     * /src/main/resources/templates/order_pickup.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */

    @GetMapping ( { "/vieworders", "/vieworders.html" } )
    public String viewOrdersForm ( final Model model ) {
        return "vieworders";
    }

    @GetMapping ( "/" )
    public String root ( final Model model ) {
        return "index";
    }

}
