
### Spring Boot Security Topics
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
