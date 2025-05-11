# Personal Blog

This is a web application for maintaining a personal blog, developed using Spring Boot. The application allows users to register, create, edit, and delete posts, as well as view posts from other users. Administrators have the ability to manage categories and assign roles to other users.

## Features

*   **User Authentication and Authorization:** Registration, login, and user role management (USER, ADMIN).
*   **Post Management:** Create, Read, Update, and Delete (CRUD) operations for posts.
*   **Post Categorization:** Ability to assign categories to posts for better organization.
*   **Category Management:** Administrators can create, edit, and delete categories.
*   **Pagination and Sorting:** Display posts with pagination and sorting capabilities.
*   **Search:** Search posts by keywords.
*   **Security:** Uses Spring Security to protect the application, including CSRF protection and HTML sanitization to prevent XSS.
*   **Data Validation:** Server-side validation of user input.
*   **Admin REST API:** (Optional, for testing) API for managing user roles.

## Technologies

*   **Java 22**
*   **Spring Boot 3.4.4**
    *   Spring Web
    *   Spring Data JPA
    *   Spring Security
    *   Spring Boot DevTools
    *   Spring Boot Starter Validation
*   **Thymeleaf:** Template engine for rendering web pages.
*   **PostgreSQL:** Relational database.
*   **Hibernate:** ORM framework for database interaction.
*   **Maven:** Project management and build automation tool.
*   **Lombok:** Library to reduce boilerplate Java code.
*   **OWASP Java HTML Sanitizer:** Library for sanitizing HTML to prevent malicious code.
*   **JUnit 5 & Mockito:** For writing and running unit tests.

## Setup and Running

### Prerequisites

*   **Java JDK 22** or higher.
*   **Apache Maven** 3.6.0 or higher.
*   **PostgreSQL** server, running and accessible.

### Database Configuration

1.  Create a PostgreSQL database. The default database name is `personal_blog_db`.
2.  If necessary, change the database connection details in the `src/main/resources/application.properties` file:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/personal_blog_db
    spring.datasource.username=myuser
    spring.datasource.password=fialka
    ```
    Replace `myuser` and `fialka` with your PostgreSQL username and password.

### Building and Running the Application

1.  Clone the repository:
    ```bash
    git clone <URL_of_your_repository>
    cd personal-blog
    ```
2.  Build the project using Maven:
    ```bash
    mvn clean install
    ```
3.  Run the application:
    ```bash
    mvn spring-boot:run
    ```
    Or run the compiled JAR file:
    ```bash
    java -jar target/personal-blog-0.0.1-SNAPSHOT.jar
    ```
The application will be available at `http://localhost:8080`.

## Testing

To run all tests, execute the command:

```bash
mvn test
```

To run tests for a specific class:

```bash
mvn test -Dtest=TestClassName
```
For example:
```bash
mvn test -Dtest=PostControllerTest
```

## Role Management (Assigning Administrator)

After registering a new user, you can assign them the administrator role to access administrative functions (e.g., category management).

This can be done by sending a POST request to the following API endpoint:

```
POST http://localhost:8080/api/admin/users/{username}/make-admin
```

Replace `{username}` with the username of the user you want to assign the administrator role to.

**Example using `curl`:**

```bash
curl -X POST http://localhost:8080/api/admin/users/your_username/make-admin
```

**Important:** This API endpoint, in the current configuration (`SecurityConfig.java`), is accessible without authentication for testing purposes. In a production environment, this endpoint must be secured and accessible only to authorized administrators.

## Project Structure

*   `src/main/java`: Application source code.
    *   `com.iwkms.personalBlog`
        *   `config`: Configuration classes (Spring Security, application constants).
        *   `controller`: Spring MVC controllers handling HTTP requests.
        *   `dto`: Data Transfer Objects for transferring data between layers.
        *   `mapper`: Classes for converting DTOs to Entities and vice versa.
        *   `model/entity`: JPA entities representing database tables.
        *   `repository`: Spring Data JPA repositories for data access.
        *   `service`: Service layer containing business logic.
        *   `validation`: Classes for custom validation.
*   `src/main/resources`: Application resources.
    *   `application.properties`: Application configuration file.
    *   `static`: Static resources (CSS, JavaScript, images).
    *   `templates`: Thymeleaf HTML templates.
*   `src/test/java`: Test source code.
*   `pom.xml`: Maven configuration file. 