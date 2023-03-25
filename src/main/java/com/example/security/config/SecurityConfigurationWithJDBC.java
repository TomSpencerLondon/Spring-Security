package com.example.security.config;
//
//import javax.sql.DataSource;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.JdbcUserDetailsManager;
//import org.springframework.security.provisioning.UserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
//@EnableWebSecurity
//@Configuration
//public class SecurityConfigurationWithJDBC {
//
//    @Bean
//    public DataSource dataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
//                .build();
//    }
//
//    @Bean
//    public UserDetailsManager users(DataSource dataSource) {
//        UserDetails userSamarth = User.withDefaultPasswordEncoder()
//                                      .username("samarth")
//                                      .password(getPasswordEncoder().encode("samarth"))
//                                      .roles("STORE_OWNER")
//                                      .build();
//        UserDetails userRohan = User.withDefaultPasswordEncoder()
//                                    .username("rohan")
//                                    .password("rohan")
//                                    .roles("STORE_CLERK")
//                                    .build();
//        
//        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
//        users.createUser(userSamarth);
//        users.createUser(userRohan);
//
//        return users;
//    }
//
////	If you don't want to encode the created password, you can write the below bean method, FYI: not recommended for Prod env
//	@Bean
//	public PasswordEncoder getPasswordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//	
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeHttpRequests()
//            .requestMatchers("/organicVeggies/viewFinancials", "/organicVeggies/makeAnnouncement")
//            .hasRole("STORE_OWNER")
//            .requestMatchers("/organicVeggies/checkInventory", "/organicVeggies/viewInventory" +
//                    "/organicVeggies/doCheckout/")
//            .hasAnyRole("STORE_OWNER", "STORE_CLERK").requestMatchers("/**").permitAll().and().formLogin();
//
//        return http.build();
//    }
//}
