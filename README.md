# UserAuthenticationService

A secure and functional user authentication system built with **Spring Boot** and **Spring Security**, demonstrating registration, login, logout, and session management. The project uses **Docker** and **Docker Compose** for containerization and includes a **PostgreSQL** database.

---

## Features

- **User Authentication:** Secure registration, login, and logout.
- **Password Management:** Passwords are hashed using **BCrypt**.
- **Persistent Login:** "Remember Me" functionality with `JdbcTokenRepositoryImpl`.
- **Containerization:** Launch the app and database easily with Docker Compose.
- **Database Management:** Supports data management via `docker exec` and `pg_dump`.

---

## Technologies Used

- **Backend:** Java, Spring Boot, Spring Security
- **Database:** PostgreSQL
- **Containerization:** Docker, Docker Compose
- **Build Tool:** Maven
- **Frontend:** HTML, CSS, JavaScript

---

## Prerequisites

- Docker & Docker Compose installed
- Java 17+
- Maven

---

## Setup & Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/vahabov007/UserAuthenticationService.git
   cd UserAuthenticationService
   ```
2. ## Setup & Run
   ### Build the Project (optional if using Docker Compose)
      ```bash
      ./mvnw clean install
      ```
   ### Run with Docker Compose
      ```bash
      docker-compose up -d
      ```
   ### Access the application
      ```bash
      http://localhost:8080
      ```
3. ## Database
   Uses PostgreSQL container.
   Default database, username, and password are configured in   application.properties or docker-compose.yml.
   Manage data using Docker commands:
      ```bash
   docker exec -it <postgres_container_name> psql -U <username> -d <database_name>
      ```
      Backup database:
      ```bash
      docker exec <postgres_container_name> pg_dump -U <username> <database_name> > backup.sql
      ```
4. ## License
MIT License

      


      


   
   
   




