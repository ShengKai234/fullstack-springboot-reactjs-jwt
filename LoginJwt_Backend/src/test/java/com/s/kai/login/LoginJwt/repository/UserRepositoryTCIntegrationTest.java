package com.s.kai.login.LoginJwt.repository;

import com.s.kai.login.LoginJwt.models.User;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {UserRepositoryTCIntegrationTest.Initializer.class})
@Testcontainers
public class UserRepositoryTCIntegrationTest extends UserRepositoryCommon {

    @Autowired
    UserRepository userRepository;

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:12")
      .withDatabaseName("integration-tests-db")
      .withUsername("sa")
      .withPassword("sa");

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    @Transactional
    public void givenUsersInDB_WhenUpdateStatusForNameModifyingQueryAnnotationNative_ThenModifyMatchingUsers() {
        System.out.println("UserRepositoryCommon");
        User user = new User();
        user.setUsername(USER_NAME_PETER);
        user.setPassword(USER_NAME_PETER_PASSWORD);
        user.setEmail(USER_EMAIL);
        userRepository.save(user);

        Optional<User> userByUsername = userRepository.findByUsername(USER_NAME_PETER);
        assertThat(userByUsername.get().getUsername()).isEqualTo(USER_NAME_PETER);
    }

}
