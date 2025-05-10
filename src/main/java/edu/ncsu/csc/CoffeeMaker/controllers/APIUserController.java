package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Barista;
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Manager;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.UserForm;
import edu.ncsu.csc.CoffeeMaker.models.enums.Role;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Class that provides multiple API endpoints for interacting with the Users
 * model.
 *
 * @author John Cheek
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIUserController extends APIController {

    /** constant for manager role */
    private static final String       ROLE_MANAGER  = "ROLE_MANAGER";

    /** constant for barista role */
    private static final String       ROLE_BARISTA  = "ROLE_BARISTA";

    /** constant for customer role */
    private static final String       ROLE_CUSTOMER = "ROLE_CUSTOMER";

    /** All roles */
    private static final List<String> ALL_ROLES     = List.of( ROLE_MANAGER, ROLE_BARISTA, ROLE_CUSTOMER );

    @Autowired
    private UserService               userService;

    /**
     * Retrieves and returns a list of all Users in the system, regardless of
     * their role
     *
     * @return list of users
     */
    @GetMapping ( BASE_PATH + "/users" )
    public List<User> getUsers () {
        return userService.findAll();
    }

    /**
     * Retrieves and returns the user with the username provided
     *
     * @param username
     *            The username of the user to be retrieved
     * @return response
     */
    @GetMapping ( BASE_PATH + "/users/{username}" )
    public ResponseEntity getUser ( @PathVariable ( "username" ) final String username ) {
        final User user = userService.findByName( username );

        return null == user
                ? new ResponseEntity( errorResponse( "No User found for id " + username ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( user, HttpStatus.OK );
    }

    /**
     * Creates a new user from the RequestBody provided, validates it, and saves
     * it to the database.
     *
     * @param userF
     *            The user to be saved
     * @return response
     */
    @PostMapping ( BASE_PATH + "/users" )
    // @PreAuthorize ( )
    public ResponseEntity createUser ( @RequestBody final UserForm userF ) {

        if ( null != userService.findByName( userF.getUsername() ) ) {
            return new ResponseEntity( errorResponse( "User with the id " + userF.getUsername() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        User user = null;
        final List<Role> rolesOnUser = userF.getRoles().stream().map( Role::valueOf ).collect( Collectors.toList() );

        try {
            if ( rolesOnUser.contains( Role.ROLE_BARISTA ) ) {
                user = new Barista( userF );
            }
            else if ( rolesOnUser.contains( Role.ROLE_CUSTOMER ) ) {
                user = new Customer( userF );
            }

            else if ( rolesOnUser.contains( Role.ROLE_MANAGER ) ) {
                user = new Manager( userF );
            }

            else {
                return new ResponseEntity(
                        errorResponse( "Could not create " + userF.getUsername() + " because of illegal user role" ),
                        HttpStatus.BAD_REQUEST );
            }

            user.setEnabled( 1 );

            userService.save( user );
            return new ResponseEntity( user, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not create " + userF.getUsername() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Gets the current logged in role.
     *
     * @return role of the currently logged in user.
     */
    @GetMapping ( BASE_PATH + "/role" )
    public ResponseEntity getRole () {
        final List<String> matchingRoles = ALL_ROLES.stream().filter( role -> hasRole( role ) )
                .collect( Collectors.toList() );

        if ( matchingRoles.isEmpty() ) {
            return new ResponseEntity( errorResponse( "UNAUTHORIZED" ), HttpStatus.UNAUTHORIZED );
        }
        // final String joinedRoles = String.join( ",", matchingRoles );
        //
        // return new ResponseEntity( joinedRoles, HttpStatus.OK );
        final Map<String, Object> rolesResponse = new HashMap<>();
        rolesResponse.put( "roles", matchingRoles );

        return ResponseEntity.ok( rolesResponse );

    }

    /**
     * Generates a set of sample users for the iTrust2 system.
     *
     * @return ResponseEntity indicating that everything is OK
     */
    @PostMapping ( BASE_PATH + "/generateUsers" )
    public ResponseEntity generateUsers () {
        final User manager = new Manager( new UserForm( "manager", "password", Role.ROLE_MANAGER, 1 ) );

        final User barista = new Barista( new UserForm( "barista", "password", Role.ROLE_BARISTA, 1 ) );

        final User customer = new Customer( new UserForm( "customer", "password", Role.ROLE_CUSTOMER, 1 ) );

        userService.save( manager );

        userService.save( barista );

        userService.save( customer );

        return new ResponseEntity( HttpStatus.OK );
    }

    /**
     * Checks if the current user has a `role`.
     *
     * @param role
     *            role to check for the user to have.
     * @return true if the user has `role`, false otherwise.
     */
    protected boolean hasRole ( final String role ) {
        // get security context from thread local
        final SecurityContext context = SecurityContextHolder.getContext();
        if ( context == null ) {
            return false;
        }

        final Authentication authentication = context.getAuthentication();
        if ( authentication == null ) {
            return false;
        }

        for ( final GrantedAuthority auth : authentication.getAuthorities() ) {
            if ( role.equals( auth.getAuthority() ) ) {
                return true;
            }
        }
        return false;
    }

    // Generative AI was used for the following function. Prompt was: Add a new
    // GET endpoint that returns the username of the
    // currently logged in user using Spring Security

    /**
     * This will return the username of the user that is currently logged in
     *
     * @return the username
     */
    @GetMapping ( BASE_PATH + "/username" )
    public ResponseEntity<Map<String, String>> getUsername () {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null && authentication.getPrincipal() instanceof UserDetails ) {
            final String username = ( (UserDetails) authentication.getPrincipal() ).getUsername();
            final Map<String, String> responseBody = new HashMap<>();
            responseBody.put( "username", username );
            return ResponseEntity.ok( responseBody );
        }
        else {
            return new ResponseEntity<>( Collections.singletonMap( "error", "No user authenticated" ),
                    HttpStatus.UNAUTHORIZED );
        }
    }

}
