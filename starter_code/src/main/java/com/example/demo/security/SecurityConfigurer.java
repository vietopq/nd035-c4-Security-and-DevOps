package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final UserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;

    public SecurityConfigurer(UserDetailsService myUserDetailsService, JwtUtil jwtUtil) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManagerBean(), jwtUtil);
        JwtBasicAuthenticationFilter jwtBasicAuthenticationFilter = new JwtBasicAuthenticationFilter(authenticationManagerBean(), jwtUtil, myUserDetailsService);

        http.csrf().disable()
                .authorizeRequests().antMatchers("/authenticate", "/login", "/api/user/create").permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtBasicAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilter(jwtAuthenticationFilter);
        // http.addFilterBefore(new JwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}