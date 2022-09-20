package com.s.kai.login.LoginJwt.repository;

import com.s.kai.login.LoginJwt.models.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryCommon {

    final String USER_EMAIL = "email@example.com";
    final String USER_EMAIL2 = "email2@example.com";
    final String USER_EMAIL3 = "email3@example.com";
    final String USER_EMAIL4 = "email4@example.com";
    final Integer INACTIVE_STATUS = 0;
    final Integer ACTIVE_STATUS = 1;
    final String USER_EMAIL5 = "email5@example.com";
    final String USER_EMAIL6 = "email6@example.com";
    final String USER_NAME_ADAM = "Adam";
    final String USER_NAME_PETER = "Peter";
    final String USER_NAME_PETER_PASSWORD = "Peter_Password";


    @Autowired
    protected UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    public void givenUserInDB_WhenFindUserByUsername_ThenReturnActiveUser() {
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
