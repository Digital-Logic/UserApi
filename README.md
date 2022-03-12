# UserApi
- User authentication and access control.
- Bitemporal database design integrated with JPA repositories.
- Dynamic property expansion integrated into JPA repositories.
- Supports multi-property sorting, filtering with bitemporal predicates.


### Built With

* Spring-Boot
* Postgres
* Redis (Spring Session data store)
* flyway
* jooq
* jpa

### Features

* User authentication and access control
* Bi-temporal database design
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

Run the application

```
./gradlew bootRun
```

### API Endpoints

|    URL    | Method | URL Params                                                        | JSON Structure                         |
| --------- | ------ |-------------------------------------------------------------------|----------------------------------------|
| /api/auth/activate-account| POST|                                                                   | { token }                              |
| /api/auth/activate-account-request| POST |                                                                   | { email }                              |
| /api/auth/reset-password | POST |                                                                   | { token, email, password }             |
| /api/auth/reset-password-request| POST |                                                                   | {email}                                |
| /api/auth/login | POST |                                                                   | { email, password }                    |
| /api/auth/logout| GET,POST |                                                                   |
| /api/user | POST   |                                                                   | {firstName, LastName, email, password} |
| /api/user | GET    | page=1, limit=25, sort=lastName, expand=roles,authorities, filter |                                        |
| /api/user/{id}| GET| expand=roles,authorities                                          |                                        |
| /api/user/{id}| PUT|| {firstName, LastName}                                             |

### Property Filtering

| Filter                | Pattern  | Description                      | Example                            | 
|-----------------------|----------|----------------------------------|------------------------------------|
| equals                | ==, =eq= | Check equality                   | lastName==Dirt                     | 
| like                  | =like=   | wildcard comparaison             | firstName=like=Jo*                 |
| ilike                 | =ilike= | Case insensitive version of like | lastName=ilike=jo*                 | 
| Less Then             | <,=lt= | Less then comparsion             | createdDate<2022-03-11T00:00:00.00 |
| Greater Then          | >, =gt= | Greater then comparsion          | createdDate>2022-03-11T00:00:00.00 |
 | Less then or equal to | <=, =lte=, |                                  |
| Greater then or equal to | >=, =gte= |                                  |

### Filtering base on BiTemporal Data
You can filter entities base on bitemporal data, but only on properties that are bitemporal.

To retrieve a list of user account that are enabled on a specific date...
```
GET /api/user?filter=accountEnabled==2022-03-11T19:16:37.617479
```


| Examples                                  | Description                                            |
|-------------------------------------------|--------------------------------------------------------|
| Get /api/user?filter=lastName=ilike=sm*th | Case insensitive filter on last name                   |
| Get /api/user?sort=-lastName,firstName    | Sort by last name in descending order, then first name |



