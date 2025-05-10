
package edu.ncsu.csc.CoffeeMaker.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    @Autowired
    protected void configure ( final AuthenticationManagerBuilder auth ) throws Exception {
        final JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> dbManager = auth.jdbcAuthentication();

        dbManager.dataSource( dataSource ).passwordEncoder( passwordEncoder() )
                .usersByUsernameQuery( "select username,password,enabled from user WHERE username = ?;" )
                .authoritiesByUsernameQuery( "select user_username, roles from user_roles where user_username=?" );
        auth.authenticationEventPublisher( defaultAuthenticationEventPublisher() );
    }

    @Override
    protected void configure ( final HttpSecurity http ) throws Exception {

        final String[] publicPatterns = new String[] { "/login*", "/register", "/images/**", "/modal", "/home",
                "generateUsers", "/api/v1/users" };

        final String[] managerPatterns = new String[] { "/ingredient", "/ingredient.html", "/inventory", "/images/**",
                "/inventory.html", "/orderhistory", "/orderhistory.html", "./images/**", "/images/almown_cp.jpg" };

        final String[] baristaPatterns = new String[] { "/recipe", "/recipe.html", "/editrecipe", "/editrecipe.html",
                "/images/**", "/vieworders", "/vieworders.html", "/orderPickup", "/orderPickup.html", "./images/**" };

        final String[] customerPatterns = new String[] { "/orderPickup", "/orderPickup.html", "/images/**", "/order",
                "/order.html", "./images/**" };

        http.authorizeRequests().antMatchers( managerPatterns ).hasRole( "MANAGER" ).antMatchers( baristaPatterns )
                .hasRole( "BARISTA" ).antMatchers( customerPatterns ).hasRole( "CUSTOMER" )
                .antMatchers( publicPatterns ).anonymous().anyRequest().authenticated().and().formLogin()
                .loginPage( "/login" ).defaultSuccessUrl( "/" ).and()// starting
                // logout
                // setup
                .logout() // Configure
                          // logout
                          // functionality
                .logoutUrl( "/logout" ) // Specifies the logout URL, defaults to
                                        // "/logout"
                .logoutSuccessUrl( "/login?logout" ) // Redirect to login page
                                                     // with query parameter
                .deleteCookies( "JSESSIONID" ) // Deletes the
                                               // JSESSIONID cookie
                .invalidateHttpSession( true ) // Invalidates the session
                .clearAuthentication( true ) // Clears authentication
                .and().csrf().csrfTokenRepository( CookieCsrfTokenRepository.withHttpOnlyFalse() );

    }

    /**
     * Bean used to generate a PasswordEncoder to hash the user-provided
     * password.
     *
     * @return The password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationEventPublisher used to assist with authentication
     *
     * @return The AuthenticationEventPublisher.
     */
    @Bean
    public DefaultAuthenticationEventPublisher defaultAuthenticationEventPublisher () {
        return new DefaultAuthenticationEventPublisher();
    }

}
