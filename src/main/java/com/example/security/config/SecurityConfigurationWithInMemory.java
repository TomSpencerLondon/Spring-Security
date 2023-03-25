package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@EnableWebSecurity
@Configuration
public class SecurityConfigurationWithInMemory {

	@Bean
	public UserDetailsService users() {
		UserDetails user = User.builder().username("samarth").password(getPasswordEncoder().encode("samarth"))
				.roles("STORE_OWNER").build();
		UserDetails admin = User.builder().username("rohan").password(getPasswordEncoder().encode("rohan"))
				.roles("STORE_CLERK").build();
		return new InMemoryUserDetailsManager(user, admin);
	}

	// If you don't want to encode the created password, you can write the below
	// bean method
	// FYI: not recommended for Prod env
	@Bean
	PasswordEncoder getPasswordEncoder() {
//		return NoOpPasswordEncoder.getInstance();
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(toH2Console());
	}

}
