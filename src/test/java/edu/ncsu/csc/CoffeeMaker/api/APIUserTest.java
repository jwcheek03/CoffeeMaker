package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Manager;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.UserForm;
import edu.ncsu.csc.CoffeeMaker.models.enums.Role;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Test for API functionality for interacting with users
 *
 * @author John Cheek
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIUserTest {

    private static final String USER_1 = "TEST_USER_1";

    private static final String USER_2 = "TEST_USER_2";

    private static final String USER_3 = "TEST_USER_3";

    private static final String PW     = "123456";

    @Autowired
    private MockMvc             mvc;

    @Autowired
    private UserService<User>   service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    // @WithMockUser ( username = "admin", roles = { "BARISTA", "CUSTOMER",
    // "MANAGER", "user" } )
    public void testCreateUsers () throws Exception {

        Assertions.assertEquals( 0, service.count(), "There should be no users in the system" );

        final UserForm u = new UserForm( USER_1, PW, Role.ROLE_MANAGER, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( 1, service.count(), "There should be one user in the system since one is created" );

        Assertions.assertTrue( service.existsByName( USER_1 ) );
        Assertions.assertFalse( service.existsByName( USER_2 ) );

        final User foundUser = service.findById( USER_1 );

        Assertions.assertEquals( 1, foundUser.getRoles().size() );
        Assertions.assertTrue( foundUser.getRoles().contains( Role.ROLE_MANAGER ) );
        Assertions.assertFalse( foundUser.getRoles().contains( Role.ROLE_CUSTOMER ) );

        Assertions.assertNotNull( foundUser, "The user should be retrivable from the database" );

        Assertions.assertEquals( Manager.class, foundUser.getClass(), "Found user should be a manager" );

        final UserForm u2 = new UserForm( USER_2, PW, Role.ROLE_BARISTA, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u2 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( 2, service.count(),
                "There should be two users in the system since another is created" );

        final User foundUser2 = service.findById( USER_2 );

        Assertions.assertEquals( 1, foundUser2.getRoles().size() );
        Assertions.assertTrue( foundUser2.getRoles().contains( Role.ROLE_BARISTA ) );

        final UserForm u3 = new UserForm( USER_3, PW, Role.ROLE_CUSTOMER, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u3 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( 3, service.count(),
                "There should be three users in the system since another is created" );

        final User foundUser3 = service.findById( USER_3 );

        Assertions.assertEquals( 1, foundUser3.getRoles().size() );
        Assertions.assertTrue( foundUser3.getRoles().contains( Role.ROLE_CUSTOMER ) );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "BARISTA", "CUSTOMER", "MANAGER" } )
    public void testRetriveUsers () throws Exception {

        Assertions.assertEquals( 0, service.count(), "There should be no users in the system" );

        final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_MANAGER, 1 );

        final User u1 = new Manager( uf );

        service.save( u1 );

        mvc.perform(
                MockMvcRequestBuilders.get( "/api/v1/users/SansUndertale" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isNotFound() );

        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/users/" + USER_1 ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        mvc.perform(
                MockMvcRequestBuilders.get( "/api/v1/users" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isForbidden() );

        Assertions.assertEquals( 1, service.count(), "There should be one user in the system" );

    }

    @Test
    @Transactional
    // @WithMockUser ( username = "user", roles = { "USER" } )
    public void testCreateInvalidUsers () throws Exception {

        Assertions.assertEquals( 0, service.count(), "There should be no users in the system" );

        final UserForm u = new UserForm( USER_1, PW, Role.ROLE_MANAGER, 1 );

        u.addRole( Role.ROLE_BARISTA.toString() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

        Assertions.assertEquals( 0, service.count(), "An invalid user should not be in the system" );

        final UserForm u1 = new UserForm( USER_1, PW, Role.ROLE_MANAGER, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u1 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u1 ) ) )
                .andExpect( MockMvcResultMatchers.status().isConflict() );

        Assertions.assertEquals( 1, service.count(), "Invalid dupicate user should not be in the system" );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "BARISTA", "CUSTOMER", "MANAGER" } )
    public void testRole () throws Exception {

        final MockHttpServletResponse response = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/role" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse();

        final String responseStr = response.getContentAsString();

        Assertions.assertTrue( responseStr.contains( "ROLE_CUSTOMER" ) );
        Assertions.assertTrue( responseStr.contains( "ROLE_BARISTA" ) );
        Assertions.assertTrue( responseStr.contains( "ROLE_MANAGER" ) );

        // Assertions.assertEquals( "roles:[" +
        // "ROLE_MANAGER,ROLE_BARISTA,ROLE_CUSTOMER", responseStr );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "user", roles = { "USER" } )
    public void testRoleUnauthorized () throws Exception {

        final MockHttpServletResponse response = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/role" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isUnauthorized() ).andReturn().getResponse();

        final String responseStr = response.getContentAsString();

        Assertions.assertTrue( responseStr.contains( "UNAUTHORIZED" ) );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "user", roles = { "USER" } )
    public void testGenerateUsers () throws Exception {

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/generateUsers" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andReturn().getResponse();

        Assertions.assertEquals( 3, service.count(), "The system should have 3 users" );

        Assertions.assertNotNull( service.findById( "manager" ) );
        Assertions.assertNotNull( service.findById( "customer" ) );
        Assertions.assertNotNull( service.findById( "barista" ) );

    }

    /**
     * This tests obtaining the username of the current user
     *
     * @throws Exception
     *             when the username is not found
     * @author asantho
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "BARISTA", "CUSTOMER", "MANAGER" } )
    public void testUsername () throws Exception {

        final MockHttpServletResponse response = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/username" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse();

        final String responseStr = response.getContentAsString();

        Assertions.assertTrue( responseStr.contains( "admin" ) );

    }

}
