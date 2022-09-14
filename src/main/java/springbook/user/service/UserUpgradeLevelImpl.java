package springbook.user.service;

import springbook.user.domain.Level;
import springbook.user.domain.User;

import static springbook.user.service.UserServiceImpl.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

public class UserUpgradeLevelImpl implements UserLevelUpgradePolicy {
    @Override
    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC: return (user.getLogin() >= MIN_LOGIN_COUNT_FOR_SILVER);
            case SILVER: return user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level");
        }
    }

    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
    }
}
