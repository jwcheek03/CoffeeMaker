package edu.ncsu.csc.CoffeeMaker.models;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

/**
 * User superclass for coffee maker. User is tied to the database using
 * Hibernate libraries.
 *
 * @author Olivia Nowell
 */
@Entity
@JsonIgnoreProperties ( value = { "password" } )
public class User extends DomainObject {

    /** For Hibernate */
    protected User () {
    }

    /**
     * Create a new user based off of the UserForm
     *
     * @param form
     *            the filled-in user form with user information
     */
    protected User ( final UserForm form ) {
        setUsername( form.getUsername() );
        if ( !form.getPassword().equals( form.getPassword2() ) ) {
            throw new IllegalArgumentException( "Passwords do not match!" );
        }
        final PasswordEncoder pe = new BCryptPasswordEncoder();
        setPassword( pe.encode( form.getPassword() ) );
        setEnabled( null != form.getEnabled() ? 1 : 0 );
        setRoles( form.getRoles().stream().map( Role::valueOf ).collect( Collectors.toSet() ) );

    }

    /** User's username */
    @Id
    @Length ( max = 20 )
    private String    username;

    /** User's password */
    private String    password;

    /** The role of the user */
    @ElementCollection ( targetClass = Role.class, fetch = FetchType.EAGER )
    @Enumerated ( EnumType.STRING )
    private Set<Role> roles;

    /** Whether or not the user is enabled */
    @Min ( 0 )
    @Max ( 1 )
    private Integer   enabled;

    public String getUsername () {
        return username;
    }

    public void setUsername ( final String username ) {
        if ( username == null ) {
            throw new IllegalArgumentException( "User needs a username" );
        }
        this.username = username;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword ( final String password ) {
        this.password = password;
    }

    public Integer getEnabled () {
        return enabled;
    }

    public void setEnabled ( final Integer enabled ) {
        this.enabled = enabled;
    }

    /**
     * Get the id of this user (aka, the username)
     *
     * @return The Username
     */
    @Override
    public String getId () {
        return getUsername();
    }

    /**
     * Get the role of this user
     *
     * @return the role of this user
     */
    public Collection<Role> getRoles () {
        return roles;
    }

    /**
     * Set the roles of this user. Throws an exception if the Set of roles
     * provided contains either a Customer or Manager role, and that role is not
     * the only one present
     *
     * @param roles
     *            the roles to set this user to
     */
    public void setRoles ( final Set<Role> roles ) {

        /* Patient & admin can't have any other roles */
        if ( ( roles.contains( Role.ROLE_MANAGER ) || roles.contains( Role.ROLE_CUSTOMER ) ) && 1 != roles.size() ) {
            throw new IllegalArgumentException( "Tried to create a Customer or Manager user with a secondary role." );
        }
        if ( roles.size() == 0 ) {
            throw new IllegalArgumentException(
                    "Tries to create a user with no roles. Users must have at least one role." );
        }

        this.roles = roles;
    }

    @Override
    public String toString () {
        return "User [username=" + username + ", password=" + password + ", roles=" + roles + ", enabled=" + enabled
                + "]";
    }

    @Override
    public int hashCode () {
        return Objects.hash( enabled, password, roles, username );
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals( enabled, other.enabled ) && Objects.equals( password, other.password )
                && Objects.equals( roles, other.roles ) && Objects.equals( username, other.username );
    }

}
