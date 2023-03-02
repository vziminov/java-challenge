### How to use this spring-boot project

- Install packages with `mvn package`
- Run `mvn spring-boot:run` for starting the application (or use your IDE)

Application (with the embedded H2 database) is ready to be used ! You can access the url below for testing it :

- Swagger UI : http://localhost:8080/swagger-ui.html
- H2 UI : http://localhost:8080/h2-console

> Don't forget to set the `JDBC URL` value as `jdbc:h2:mem:testdb` for H2 UI.



### Instructions

- download the zip file of this project
- create a repository in your own github named 'java-challenge'
- clone your repository in a folder on your machine
- extract the zip file in this folder
- commit and push

- Enhance the code in any ways you can see, you are free! Some possibilities:
  - Add tests
  - Change syntax
  - Protect controller end points
  - Add caching logic for database calls
  - Improve doc and comments
  - Fix any bug you might find
- Edit readme.md and add any comments. It can be about what you did, what you would have done if you had more time, etc.
- Send us the link of your repository.

#### Restrictions
- use java 8


#### What we will look for
- Readability of your code
- Documentation
- Comments in your code 
- Appropriate usage of spring boot
- Appropriate usage of packages
- Is the application running as expected
- No performance issues

#### Your experience in Java

Please let us know more about your Java experience in a few sentences. For example:

- I have 3 years experience in Java and I started to use Spring Boot from last year
- I'm a beginner and just recently learned Spring Boot
- I know Spring Boot very well and have been using it for many years

### !!! Without additional configuration application login:password is admin:admin !!!

#### Java experience:
I've been working with Java for 7 years, of which 5 years specifically with Spring.

#### Comments:
1. Updated libraries as much as possible:
   1. Spring 6 requires Java 17 and assignment requires Java 8
   2. SpringFox above 2.9.2 has too many bugs and not maintained for years - I would strongly recommend to switch to 
    OpenApi if possible according to business expectations
   3. No proprietary or copy-left licensed third-parties used
2. In EmployeeController renamed and changed endpoints to more meaningful - for the client using such endpoints as it 
   was could be pretty painful and error-prone. Plus, added swagger documentation and examples.
3. Specifically added DAO layer which looks like over-engineering here, but is useful when project gets more 
   complicated:
   1. Service layer should be agnostic of data structure (as an example, shouldn't operate with entities)
   2. Data consistency related business logic could be put in DAO layer
   3. Data usage optimizations could be added in DAO layer
   4. Sometimes it could be further separated into DAO and DataService layers
4. Employee salary could be sensitive information, so it's better to store encrypted. Department with name is not 
   enough to identify a person so ok not to encrypt.
   1. There is a property for the AES192 secret file (a file with 192 bits of data) - that makes it possible to
      provide secret as a docker/kubernetes secret file
5. Added input validation - cases handled by spring (like setting string to salary) look ugly, but to change it would 
   require writing a full error handling mechanism
6. Added unit tests with line coverage 79% - controllers and spring configurations are not covered.
   1. Testing it just for the sake of line coverage makes little practical sense - things like endpoints, authentication 
      and authorization should be tested with integration tests
7. Added primitive basic authentication and authorization system:
   1. There is JdbcUserDetailsManager from spring, but it's quite ugly and not extendable - for example, using id 
      instead of username as a DB key would provide more versatility in the future
   2. As I didn't have much time this is a minimal working implementation - better than giving Employee information
      to everyone
   3. Correct design should contain groups and possibility to map multiple roles to user/group
   4. For microservice architecture oauth2/OIDC server integration is usually a correct solution
   5. There is a property for the admin password secret file - that makes it possible to
      provide secret as a docker/kubernetes secret file
   6. Browser popup for basic auth is buggy - it would remember credentials and sneakily send it after you remove session 
      cookie - better use swagger auth button + remove cookie (don't want to sacrifice WWW-Authenticate because of browser issue)

#### Left out of scope as don't have enough time:
1. More granular and unified error handling
2. Re-encryption of data in case of the secret change - we store everything in embedded H2 anyway
3. Logging and security audit logging
4. More granular user/roles/groups/permissions system
5. TLS - we should not permit unencrypted communication for real applications, but it is literally few lines to configure 
   while some random self-signed certificate would make your verification more ugly
6. Caching logic for database calls - I had pretty bad experience with such solutions as it would always affect
   user experience. Usually performance problems arise from sloppy use of JPA and bad design - so to large degree
   there are ways to optimize the performance/scale horizontally. Another option is use JDBC directly - it's much faster, 
   though would become unmaintainable quite fast with the service codebase increase in size.
