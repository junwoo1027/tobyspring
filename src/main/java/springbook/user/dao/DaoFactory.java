package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceTx;
import springbook.user.service.UserUpgradeLevelImpl;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {
	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource ();

		dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost/springbook?characterEncoding=UTF-8");
		dataSource.setUsername("spring");
		dataSource.setPassword("book");

		return dataSource;
	}

	@Bean
	public UserDaoJdbc userDao() {
		UserDaoJdbc userDaoJdbc = new UserDaoJdbc();
		userDaoJdbc.setDataSource(dataSource());
		return userDaoJdbc;
	}

	@Bean
	public UserServiceImpl userServiceImpl() {
		UserServiceImpl userService = new UserServiceImpl();

		userService.setUserDao(userDao());
		userService.setUpgradeLevelPolicy(upgradeLevelImpl());
		userService.setMailSender(javaMailSender());
		return userService;
	}

	@Bean
	public UserServiceTx userService() {
		UserServiceTx userServiceTx = new UserServiceTx();
		userServiceTx.setUserService(userServiceImpl());
		userServiceTx.setTransactionManager(transactionManager());
		return userServiceTx;
	}

	@Bean
	public UserUpgradeLevelImpl upgradeLevelImpl() {
		UserUpgradeLevelImpl userUpgradeLevel = new UserUpgradeLevelImpl();
		return userUpgradeLevel;
	}

	@Bean
	public DataSourceTransactionManager transactionManager() {
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource());
		return dataSourceTransactionManager;
	}

	@Bean
	public DummyMailSender javaMailSender() {
		DummyMailSender javaMailSender = new DummyMailSender();
		return javaMailSender;
	}
}
