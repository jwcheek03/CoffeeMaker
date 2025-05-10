package edu.ncsu.csc.CoffeeMaker.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

@Controller
public class ManagerController {

    @RequestMapping ( value = "/manager/index" )
    @PreAuthorize ( "hasAnyRole('ROLE_MANAGER')" )
    public String index ( final Model model ) {
        return Role.ROLE_MANAGER.getLanding();
    }

    @GetMapping ( value = "/manager/inventory" )
    @PreAuthorize ( "hasAnyRole('ROLE_MANAGER')" )
    public String inventoryForm ( final Model model ) {
        return "/manager/inventory";
    }

    @GetMapping ( value = "/manager/ingredient" )
    @PreAuthorize ( "hasAnyRole('ROLE_MANAGER')" )
    public String addIngredientForm ( final Model model ) {
        return "/manager/ingredient";
    }

}
