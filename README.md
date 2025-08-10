This project is a fully functional user authentication system developed using Spring Boot and Spring Security. The application demonstrates a secure login, registration, and session management mechanism, which is a fundamental component of modern software development.

The project's architecture is built on containerization principles using Docker and Docker Compose together with a PostgreSQL database. This approach ensures that the application can be deployed and run seamlessly in any environment.

Key Features:

User Authentication: Secure registration, login, and logout functionalities.

Password Management: Passwords are securely stored using the BCrypt hashing algorithm.

Persistent Login: The Remember Me function is implemented using Spring Security's JdbcTokenRepositoryImpl.

Containerization: The application and database can be easily launched with a single docker compose up command.

Database Management: The repository includes instructions for managing database data and synchronization using commands like docker exec and pg_dump.

Technologies Used:

Backend: Java, Spring Boot, Spring Security

Database: PostgreSQL

Containerization: Docker, Docker Compose

Build Tool: Maven
