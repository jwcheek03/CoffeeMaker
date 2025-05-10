package edu.ncsu.csc.CoffeeMaker.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

@Controller
public class CustomerController {

    @RequestMapping ( value = "/customer/index" )
    @PreAuthorize ( "hasAnyRole('ROLE_CUSTOMER')" )
    public String index ( final Model model ) {
        return Role.ROLE_MANAGER.getLanding();
    }

    @GetMapping ( value = "/customer/orderBeverage" )
    @PreAuthorize ( "hasAnyRole('ROLE_CUSTOMER')" )
    public String createOrderForm ( final Model model ) {
        return "/customer/orderBeverage";
    }

    @GetMapping ( value = "/customer/viewOrderHistory" )
    @PreAuthorize ( "hasAnyRole('ROLE_CUSTOMER')" )
    public String viewOrderHistory ( final Model model ) {
        return "/customer/viewOrderHistory";
    }

}
