# BetAPI

A Spring Boot application.

## Description

BetAPI is a Spring Boot 4.0.0 application built with Java 21 and Maven.

## Prerequisites

- Java 21 or higher
- Maven 3.6+ (or use the included Maven wrapper)

## Getting Started

### Build the project

```bash
./mvnw clean install
```

### Run the application

```bash
./mvnw spring-boot:run
```

Or build and run the JAR:

```bash
./mvnw clean package
java -jar target/betapi-0.0.1-SNAPSHOT.jar
```

### Run tests

```bash
./mvnw test
```

## Project Structure

```
betapi/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/betapi/
│   │   │       └── BetapiApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/example/betapi/
│               └── BetapiApplicationTests.java
├── pom.xml
└── README.md
```

## Technologies

- **Spring Boot 4.0.0** - Application framework
- **Java 21** - Programming language
- **Maven** - Build tool

## License

This project is licensed under the terms specified in the LICENSE file.

## Contact

For questions or support, please contact the project maintainers.

