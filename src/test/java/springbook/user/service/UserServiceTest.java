package springbook.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springbook.user.dao.DaoFactory;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static springbook.user.service.UserService.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoFactory.class)
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    private List<User> users;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
                new User("1", "김준우1", "123", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER - 1, 0),
                new User("2", "김준우2", "123", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER, 0),
                new User("3", "김준우3", "123", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
                new User("4", "김준우4", "123", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
                new User("5", "김준우5", "123", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    void upgradeLevels() {
        userDao.deleteAll();

        for (User user : users) {
            userDao.add(user);
        }

        userService.upgradeLevels();
        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);
    }

    @Test
    void add() {
        userDao.deleteAll();

        User user = users.get(4);
        User user1 = users.get(0);
        user1.setLevel(null);

        userService.add(user);
        userService.add(user1);

        User getUser1 = userDao.get(user.getId());
        User getUser2 = userDao.get(user1.getId());

        assertThat(getUser1.getLevel()).isEqualTo(user.getLevel());
        assertThat(getUser2.getLevel()).isEqualTo(Level.BASIC);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User upgradeUser = userDao.get(user.getId());
        if (upgraded) {
            assertEquals(upgradeUser.getLevel(), user.getLevel().getNext());
        } else {
            assertEquals(upgradeUser.getLevel(), user.getLevel());
        }
    }
}