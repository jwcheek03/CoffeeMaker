package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * @author John Cheek
 */
public interface UserRepository <T extends User> extends JpaRepository<T, String> {

    /**
     * Checks if a User with the given username exists
     *
     * @param name
     *            Username to check
     * @return if user exists
     */
    public boolean existsByUsername ( String name );

    /**
     * Finds a User with the given Username
     *
     * @param username
     *            Username to check
     * @return Matching user, if any
     */
    public User findByUsername ( String username );

}
