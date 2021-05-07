# UserApi

## Getting Started

### Built With

* Spring-Boot
* Postgres
* Redis (Spring Session data store)
* flyway
* jooq
* jpa

### Features

* User authentication and access control
* Api endpoints supports dynamic property expansion
* Api endpoints supports filtering, sorting, searching

### Prerequisites

[Docker Compose](https://docs.docker.com/compose/install/) is required to start dependent services (Postgres, Redis,
flyway), and to setup the testing environment.

```
docker-compose up -d
docker-compose -f docker-compose.test.yml up -d
```

Once the docker containers are up and running, build the server.

```
./gradlew build
```

Run the tests.

```
./gradlew test
```

To run the application

```
./gradlew bootRun
```

### API Endpoints

|    URL    | Method | URL Params | Data Params |
| --------- | ------ | ---------- | ----------- |
| /api/auth/activate-account| POST| | { token } |
| /api/auth/activate-account-request| POST | | { email } |
| /api/auth/reset-password | POST | | { token, email, password } |
| /api/auth/reset-password-request| POST | | {email} |
| /api/user | POST   |            | {firstName, LastName, email, password} |
| /api/user | GET    | page, limit, sort, expand, filter | |
| /api/user/{id}| GET| expand     | |
| /api/user/{id}| PUT|| {firstName, LastName} |
