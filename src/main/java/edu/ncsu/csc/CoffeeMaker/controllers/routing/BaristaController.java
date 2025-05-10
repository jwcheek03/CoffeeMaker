package edu.ncsu.csc.CoffeeMaker.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

@Controller
public class BaristaController {

    @RequestMapping ( value = "/barista/index" )
    @PreAuthorize ( "hasAnyRole('ROLE_BARISTA')" )
    public String index ( final Model model ) {
        return Role.ROLE_BARISTA.getLanding();
    }

    @GetMapping ( value = "/barista/recipe" )
    @PreAuthorize ( "hasAnyRole('ROLE_BARISTA')" )
    public String createRecipeForm ( final Model model ) {
        return "/barista/recipe";
    }

    @GetMapping ( value = "/barista/editrecipe" )
    @PreAuthorize ( "hasAnyRole('ROLE_BARISTA')" )
    public String editRecipeForm ( final Model model ) {
        return "/barista/editrecipe";
    }

    // TODO: Update
    @GetMapping ( value = "/barista/fulfillOrder" )
    @PreAuthorize ( "hasAnyRole('ROLE_BARISTA')" )
    public String fulfillOrderForm ( final Model model ) {
        return "/barista/fulfillOrder";
    }

}
