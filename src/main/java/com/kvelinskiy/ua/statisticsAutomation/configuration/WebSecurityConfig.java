package com.kvelinskiy.ua.statisticsAutomation.configuration;

import com.kvelinskiy.ua.statisticsAutomation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.sql.DataSource;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    public WebSecurityConfig(UserService userService) {
        this.userService = userService;
    }

    //roles admin allow to access/admin/** **
    //roles user allow to access/user/** **
    //custom 403 access denied handler
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //, "/css/**", "/js/**", "/images/**"
        http.authorizeRequests()
                .antMatchers("/", "/index", "/registration", "/error").permitAll()
                .antMatchers( "/user/**").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
                .antMatchers( "/admin/**").access("hasRole('ROLE_ADMIN')")
//                .antMatchers("/user/setMessage").access("hasAnyRole('USER')")
//                .anyRequest().authenticated()
                .and().formLogin()
                .loginPage("/login").permitAll()
                .and().exceptionHandling().accessDeniedPage("/access-denied.html")
                .and()
                .logout()
                .logoutSuccessUrl("/index")
                .permitAll();
        http.csrf().disable();
    }
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(

                // static
                "/css/**",
                "/js/**",
                "/images/**"
        );
    }

    @Bean(name = "passwordEncoder")
    public PasswordEncoder passwordencoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordencoder());
    }
}