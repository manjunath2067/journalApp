# Journal App

**Journal App** - Version: 0.0.17-SNAPSHOT

**Journal App** is an end-to-end encrypted (E2EE) journal application built using Spring Boot. It provides a secure platform for users to create, read, update, and delete journal entries. The application uses MongoDB as the database and includes user authentication and authorization features.

## Features

- **User Authentication and Authorization**: Secure login and access control using Spring Security.
- **CRUD Operations**: Create, read, update, and delete journal entries.
- **End-to-End Encryption**: Ensures that journal entries are securely stored and transmitted.
- **MongoDB Integration**: Uses MongoDB for data storage.
- **RESTful API**: Exposes endpoints for journal entry operations.
- **OpenFeign Client**: Integrates with external services using Spring Cloud OpenFeign.
- **Input Validation**: Validates user input using Jakarta Validation API.
- **Weather Integration**: Fetches weather information for locations.
- **Text-to-Speech Conversion**: Converts journal text to speech audio.

## Technologies Used

- **Java 17**
- **Spring Boot 3.2.8**
- **Spring Data MongoDB**
- **Spring Security**
- **Spring Cloud OpenFeign**
- **Jakarta Validation API**
- **Maven**
- **Lombok**
- **MongoDB**

## Prerequisites

- **Java 17**
- **Maven**
- **MongoDB**

## Setup Instructions

1. **Clone the repository**:
   ```sh
   git clone https://github.com/manjunath2067/journalApp.git
   cd journalApp
   ```

2. **Configure MongoDB**:
    - Install MongoDB if not already installed
    - Create a database for the application
    - Update `application.properties` or `application.yml` with your MongoDB connection details

3. **Build the application**:
   ```sh
   mvn clean install
   ```

4. **Run the application**:
   ```sh
   mvn spring-boot:run
   ```

   Alternatively, run the JAR file:
   ```sh
   java -jar target/journalApp-0.0.1-SNAPSHOT.jar
   ```

5. **Access the application**:
    - The application will be available at `http://localhost:8080`
    - API endpoints can be accessed via this base URL

## API Endpoints

The application exposes the following RESTful endpoints:

### Public Endpoints

- `GET /public/health-check` - Check service health
- `POST /public/create-user` - Register a new user

### Journal Entries

- `GET /journal` - Retrieve all journal entries for the logged-in user
- `POST /journal` - Create a new journal entry
- `GET /journal/id/{myId}` - Retrieve a specific entry by ID
- `PUT /journal/id/{id}` - Update an existing entry
- `DELETE /journal/id/{deleteId}` - Delete a journal entry

### User Management

- `PUT /users` - Update the current user's information
- `DELETE /users` - Delete the current user's account

### Admin Operations

- `GET /admin/all-users` - Retrieve all users (admin only)
- `POST /admin/create-admin-user` - Create a new admin user

### Weather Information

- `GET /weather` - Get weather information for the current user's location

### Text-to-Speech

- `POST /api/tts?text={text}` - Convert text to speech and return audio file

## Security

This application implements end-to-end encryption for journal entries, ensuring that:
- Data is encrypted before being stored in the database
- Only the authenticated user can decrypt and view their own entries
- Communications between client and server are secured

## Configuration

The application can be configured through the `application.properties` or `application.yml` file:

```yaml
# Server configuration
server:
  port: 8080

# MongoDB configuration
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/journaldb

# Security configuration
security:
  encryption:
    secret-key: your-secret-key
```

## SonarCloud Integration

This project is configured for code quality analysis with SonarCloud:
- Organization: manjunath2067
- URL: https://sonarcloud.io

## Development

### Running Tests

```sh
mvn test
```

### Building for Production

```sh
mvn clean package -P prod
```

## Contact

If you encounter any issues or have questions about the Journal App, please contact:
- Email: manjunathbt2067@gmail.com
- GitHub Issues: [Create an issue](https://github.com/manjunath2067/journalApp/issues)

## License

[MIT License](LICENSE)