package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.repositories.UserRepository;

/**
 * @author John Cheek
 */
@Component
@Transactional
@Primary
public class UserService <T extends User> extends Service<T, String> {

    @Autowired
    private UserRepository<User> userRepository;

    @SuppressWarnings ( "unchecked" )
    @Override
    protected JpaRepository<T, String> getRepository () {
        return (JpaRepository<T, String>) userRepository;
    }

    /**
     * Finds a User with the given username
     *
     * @param username
     *            Username to search
     * @return Matching user, if any
     */
    public User findByName ( final String username ) {
        return userRepository.findByUsername( username );
    }

    /**
     * Checks if a User with the provided Username exists
     *
     * @param name
     *            Username to check
     * @return Whether user exists
     */
    public boolean existsByName ( final String name ) {
        return userRepository.existsByUsername( name );
    }

    // public Collection<Role> getRoleByUsername ( final String username ) {
    // return userRepository.findByUsername( username ).getRoles();
    // }

}
