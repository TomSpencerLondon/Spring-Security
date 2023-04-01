
### Spring Boot Security Topics
https://github.com/sam253narula/Spring-Boot-Security-3.0.2

1. Introduction and Basic Concepts
- what is security?
    - Hardware security
    - network security (transport layer security, firewalls) - AWS security group = firewall
    - operating system (user access control, software restrictions and Application Security)
- spring security is application security
- Also web based security (form based + jwt security)

#### What is application security
- good coding practices
- data handling
- application user access control
    - some users all APIs
    - some users certain APIs
    - restrict authorization aswell as authentication

#### What is Spring Security
- Authentication + Authorization
- Secures enterprise applications
- roles and access

#### Basic Terminologies
- termiologies
- types of authentication
- understanding filter
- Authentication = user name password + proving they are who they are
- Authorization = once authenticated allow access to resources
- Principal = currently logged in user
- Granted Authority = What a particular user is allowed to do
- Role = Users can be given roles - Owner can do everything
    - Clerk is not able to view financials just check inventory

### Types of Authentication
- knowledge based authentication = username password
- possession based authentication = phone and mfa
- Multi Factor Authentication = combination of knowledge based and possession based


#### Filters Spring Security
Spring security is a filter for access to Spring controller methods:
![image](https://user-images.githubusercontent.com/27693622/227707037-c60ec7b6-cb2c-4cd6-90be-ccc448147150.png)

These are the APIs for our example application:
![image](https://user-images.githubusercontent.com/27693622/227707208-55290641-e049-4076-86f4-820657616164.png)

There is make Announcement to all screens. View inventory with prices. ViewFinancials shows us the finances.
When we have implemented Spring Security we will be able to access the view API but not access the APIs.

### Different implementations
- What are the implementations? (form based + token based security)
- Inject Spring security dependency in POM file
- default username + password (in console) comes with spring security POM:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

This gives 403 forbidden result for POST action of our API. After logging in we can see get requests.
We need to disable csrf to access POST requests. We also get a log out form and an error form.

#### Disabling default username
To disable default username and password:
```properties
spring.security.user.name=tom
spring.security.user.password=admin
```
This disables the default username and password. We can also exclude Spring security:
```properties

# 1) To disable the Default username password Spring Security 
spring.autoconfigure.exclude[0]=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
spring.autoconfigure.exclude[1]=org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
```
This is not a good practice.

#### Customising login with Security Configuration
- Create Security Configuration class for in memory authentication

```java
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
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(toH2Console());
    }

}
```
Here we use the PasswordEncoder to get rid of the following exception:
```bash
java.lang.IllegalArgumentException: There is no Password Encoder mapped for the id "null".
```
The PasswordEncoder is kept in org.springframework.crypto.password:
https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/password/PasswordEncoder.html

This article is quite good on the PasswordEncoder:
https://www.baeldung.com/spring-security-5-default-password-encoder

We add the following to ignore SpringBoot security with h2:
```java
@EnableWebSecurity
@Configuration
public class SecurityConfigurationWithInMemory {
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers(toH2Console());
  }
}

```


#### Encrypting passwords with Bcrypt
Bcrypt is a password encoder used to encrypt passwords. It is a one way password encoder. The password encrypted with
password encoder cannot be decrypted into ordinary text. Instead we encrypt original password and compare the two hashes.

```java
import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityConfigurationWithInMemory {

  @Bean
  public UserDetailsService users() {
    UserDetails user = User.builder().username("samarth").password(getPasswordEncoder().encode("samarth"))
            .roles("STORE_OWNER").build();
    log.info("Hashed  password" + getPasswordEncoder().encode("samarth"));
    UserDetails admin = User.builder().username("rohan").password(getPasswordEncoder().encode("rohan"))
            .roles("STORE_CLERK").build();
    return new InMemoryUserDetailsManager(user, admin);
  }
}
```
The above code takes the password given and compares the two hashes between the password given and the password stored in
memory. We can see the password in the logs:
```bash
2023-04-01T09:38:58.095+01:00  INFO 159663 --- [  restartedMain] .e.s.c.SecurityConfigurationWithInMemory : Hashed  password$2a$10$wy/G.aCMv7TGPjO7eioei.hTV9/XpSvVnfEDua5eN3/EejKRzmYMm
```
This is just for demo purposes. We should not log passwords in production. If we didn't want to implement password encoding
we would use:
```bash
NoOpsPasswordEncoder.getInstance()
```
Again, this is not best practice and is in fact deprecated. BCrypt is better practice.

### JDBC Authentication schema, users, password and role

Now we will learn about JDBC based authentication. This video was useful for configuring Jdbc security connection:
https://github.dev/danvega/jdbc-users

https://www.youtube.com/watch?v=d7ZmZFbE_qY

This was my configuration:
```java

@Configuration
@EnableWebSecurity
public class SecurityConfigurationWithJDBC {

    @Bean
    DataSource dataSource() {
        SimpleDriverDataSourceFactory factory = new SimpleDriverDataSourceFactory();
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setDataSourceFactory(factory)
                .setName("dashboard")
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    }

    @Bean
    JdbcUserDetailsManager users(DataSource dataSource, PasswordEncoder encoder) {
		UserDetails user = User.builder().username("samarth").password(passwordEncoder().encode("samarth"))
				.roles("STORE_OWNER").build();

        UserDetails admin = User.builder().username("rohan").password(passwordEncoder().encode("rohan"))
				.roles("STORE_CLERK").build();

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.createUser(user);
        jdbcUserDetailsManager.createUser(user);
        return jdbcUserDetailsManager;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(toH2Console());
	}
}

```



