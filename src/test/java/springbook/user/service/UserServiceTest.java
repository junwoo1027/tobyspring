package springbook.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.DaoFactory;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static springbook.user.service.UserServiceImpl.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoFactory.class)
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private UserDao userDao;
//    @Autowired
//    private UserLevelUpgradePolicy userLevelUpgradePolicy;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;
    private List<User> users;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
                new User("1", "김준우1", "123", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER - 1, 0, "junwoo1027@naver.com"),
                new User("2", "김준우2", "123", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER, 0, "junwoo1027@ndotlight.com"),
                new User("3", "김준우3", "123", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "junwoo1027@gmail.com"),
                new User("4", "김준우4", "123", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "junwoo1027@hanmai.net"),
                new User("5", "김준우5", "123", Level.GOLD, 100, Integer.MAX_VALUE, "junwoo1027@naver.com")
        );
    }

    @Test
    @DirtiesContext
    void upgradeLevels() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao userDaoMock = mock(UserDao.class);
        when(userDaoMock.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(userDaoMock);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        verify(userDaoMock, times(2)).update(any(User.class));
        verify(userDaoMock).update(users.get(1));
        assertEquals(users.get(1).getLevel(), Level.SILVER);
        verify(userDaoMock).update(users.get(3));
        assertEquals(users.get(3).getLevel(), Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertEquals(mailMessages.get(0).getTo()[0], users.get(1).getEmail());
        assertEquals(mailMessages.get(1).getTo()[0], users.get(3).getEmail());
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

    @Test
    void upgradeAllOrNothing() {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
//        testUserService.setUpgradeLevelPolicy(this.userLevelUpgradePolicy);
        testUserService.setMailSender(mailSender);

        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(testUserService);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern("upgradeLevels");

        UserService txUserService= (UserService)Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {UserService.class},
                txHandler
        );

        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException");
        } catch (TestUserServiceException e) {

        }

        checkLevelUpgraded(users.get(1), false);
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