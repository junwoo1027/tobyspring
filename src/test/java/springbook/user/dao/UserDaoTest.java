package springbook.user.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoFactory.class)
public class UserDaoTest {
    @Autowired
    private  UserDao dao;
    @Autowired
    private DataSource dataSource;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setup() {
        user1 = new User("1", "김준우1", "123", Level.BASIC, 1, 0, "junwoo1027@naver.com");
        user2 = new User("2", "김준우2", "123", Level.SILVER, 55, 10, "junwoo1027@gmail.com");
        user3 = new User("3", "김준우3", "123", Level.GOLD, 100, 40, "junwoo1027@ndotlight.com");
    }

    @Test
    public void add() {
        dao.deleteAll();
        dao.add(user3);

        int count = dao.getCount();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void getAll() {
        dao.deleteAll();

        List<User> users = dao.getAll();
        assertThat(users.size()).isEqualTo(0);

        dao.add(user1);
        List<User> users1 =  dao.getAll();
        assertThat(dao.getCount()).isEqualTo(1);
        this.checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 =  dao.getAll();
        assertThat(dao.getCount()).isEqualTo(2);
        this.checkSameUser(user1, users2.get(0));
        this.checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 =  dao.getAll();
        assertThat(dao.getCount()).isEqualTo(3);
        this.checkSameUser(user1, users3.get(0));
        this.checkSameUser(user2, users3.get(1));
        this.checkSameUser(user3, users3.get(2));
    }

    @Test
    public void addAndGet() throws SQLException {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown"));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        User userGet1 = dao.get(user1.getId());
        this.checkSameUser(user1, userGet1);

        User userGet2 = dao.get(user2.getId());
        this.checkSameUser(user2, userGet2);
    }

    @Test
    public void count() throws SQLException {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        assertThat(dao.getCount()).isEqualTo(1);

        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        dao.add(user3);
        assertThat(dao.getCount()).isEqualTo(3);
    }

    @Test()
    public void getUserFailure() throws SQLException {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
    }

    @Test
    void duplicateKey() {
        dao.deleteAll();
        dao.add(user1);

        assertThrows(DuplicateKeyException.class, () -> dao.add(user1));
    }

    @Test
    void sqlExceptionTranslate() {
        dao.deleteAll();

        try {
            dao.add(user1);
            dao.add(user1);
        } catch (DuplicateKeyException e) {
            SQLException sqlEx = (SQLException) e.getRootCause();
            SQLExceptionTranslator set =
                    new SQLErrorCodeSQLExceptionTranslator(this.dataSource);

            assertThat(set.translate(null, null, sqlEx)).isInstanceOf(DuplicateKeyException.class);
        }
    }

    @Test
    void update() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);

        user1.setName("주누");
        user1.setPassword("1027");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1Update = dao.get(user1.getId());
        checkSameUser(user1Update, user1);
        User getUser = dao.get(user2.getId());
        checkSameUser(getUser, user2);
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getName()).isEqualTo(user2.getName());
        assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
        assertThat(user1.getLogin()).isEqualTo(user2.getLogin());
        assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
        assertThat(user1.getRecommend()).isEqualTo(user2.getRecommend());
    }
}
