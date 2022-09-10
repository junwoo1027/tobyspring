package springbook.user.service;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;

public class UserService {
    private UserDao userDao;
    public static final int MIN_LOGIN_COUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;
    private UserLevelUpgradePolicy upgradeLevelPolicy;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUpgradeLevelPolicy(UserLevelUpgradePolicy upgradeLevelPolicy) {
        this.upgradeLevelPolicy = upgradeLevelPolicy;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();

        for (User user : users) {
            if (this.upgradeLevelPolicy.canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }

    public void upgradeLevel(User user) {
        this.upgradeLevelPolicy.upgradeLevel(user);
        userDao.update(user);
    }
}
