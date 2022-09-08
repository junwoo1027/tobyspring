package springbook.user.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springbook.user.domain.User;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoFactory.class)
public class UserDaoTest {
    @Autowired
    private  UserDao dao;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setup() {
        user1 = new User("1", "김준우1", "123");
        user2 = new User("2", "김준우2", "123");
        user3 = new User("3", "김준우3", "123");
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
        assertThat(userGet1.getName()).isEqualTo(user1.getName());
        assertThat(userGet1.getPassword()).isEqualTo(user1.getPassword());

        User userGet2 = dao.get(user2.getId());
        assertThat(userGet2.getName()).isEqualTo(user2.getName());
        assertThat(userGet2.getPassword()).isEqualTo(user2.getPassword());
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

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getName()).isEqualTo(user2.getName());
        assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
    }
}
