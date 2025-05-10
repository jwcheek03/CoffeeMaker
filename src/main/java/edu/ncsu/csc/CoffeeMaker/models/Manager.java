package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

/**
 * Represents a Manager stored in the CoffeeMaker system
 *
 * @author Olivia Nowell
 *
 */
@Entity
@JsonIgnoreProperties ( value = { "password" } )
public class Manager extends User {

    public Manager () {

    }

    /**
     * Creates a Manager from the provided UserForm
     *
     * @param uf
     *            UserForm to build a manager
     */
    public Manager ( final UserForm uf ) {
        super( uf );
        if ( !getRoles().contains( Role.ROLE_MANAGER ) ) {
            throw new IllegalArgumentException( "Attempted to create a Manager record for a non-manager user!" );
        }
    }

}
