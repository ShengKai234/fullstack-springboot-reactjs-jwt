# LoginJwt

## SQL init
```roomsql
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```

## Run
```shell
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## Jar
```shell
./gradlew bootJar --args='--spring.profiles.active=dev'
```