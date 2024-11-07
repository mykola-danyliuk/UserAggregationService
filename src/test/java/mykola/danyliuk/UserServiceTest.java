package mykola.danyliuk;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserServiceTest {

    @ClassRule
    public static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.3")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass");

    @ClassRule
    public static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.4.3")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass");

    @Autowired
    private UserService userService;

    @BeforeAll
    public static void setUp() throws Exception {
        postgreSQLContainer.setPortBindings(List.of("5432:5432"));
        postgreSQLContainer.start();
        try (Connection connection = postgreSQLContainer.createConnection("")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE users (user_id SERIAL PRIMARY KEY, login VARCHAR(255), first_name VARCHAR(255), last_name VARCHAR(255))");
                statement.execute("INSERT INTO users (login, first_name, last_name) VALUES ('jdoe', 'John', 'Doe')");
            }
        }

        mySQLContainer.setPortBindings(List.of("3306:3306"));
        mySQLContainer.start();
        try (Connection connection = mySQLContainer.createConnection("")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE users (user_id INT AUTO_INCREMENT PRIMARY KEY, login VARCHAR(255), first_name VARCHAR(255), last_name VARCHAR(255))");
                statement.execute("INSERT INTO users (login, first_name, last_name) VALUES ('asmith', 'Alice', 'Smith')");
            }
        }
    }

    @AfterAll
    public static void tearDown() {
        postgreSQLContainer.stop();
        mySQLContainer.stop();
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAllUsers(null, null, null);
        assertThat(users).hasSize(2);
        User jdoe = users.stream().filter(user -> user.username().equals("jdoe")).findFirst().orElseThrow();
        assertThat(jdoe.name()).isEqualTo("John");
        assertThat(jdoe.surname()).isEqualTo("Doe");
        User asmith = users.stream().filter(user -> user.username().equals("asmith")).findFirst().orElseThrow();
        assertThat(asmith.name()).isEqualTo("Alice");
        assertThat(asmith.surname()).isEqualTo("Smith");
    }

    @Test
    public void testGetUsersByLogin() {
        List<User> users = userService.getAllUsers("jdoe", null, null);
        assertThat(users).hasSize(1);
        assertThat(users.get(0).username()).isEqualTo("jdoe");
        assertThat(users.get(0).name()).isEqualTo("John");
        assertThat(users.get(0).surname()).isEqualTo("Doe");
    }

    @Test
    public void testGetUsersByFirstName() {
        List<User> users = userService.getAllUsers(null, "John", null);
        assertThat(users).hasSize(1);
        assertThat(users.get(0).username()).isEqualTo("jdoe");
        assertThat(users.get(0).name()).isEqualTo("John");
        assertThat(users.get(0).surname()).isEqualTo("Doe");
    }

    @Test
    public void testGetUsersByLastName() {
        List<User> users = userService.getAllUsers(null, null, "Doe");
        assertThat(users).hasSize(1);
        assertThat(users.get(0).username()).isEqualTo("jdoe");
        assertThat(users.get(0).name()).isEqualTo("John");
        assertThat(users.get(0).surname()).isEqualTo("Doe");
    }

    @Test
    public void testGetUsersByLoginAndFirstName() {
        List<User> users = userService.getAllUsers("jdoe", "John", null);
        assertThat(users).hasSize(1);
        assertThat(users.get(0).username()).isEqualTo("jdoe");
        assertThat(users.get(0).name()).isEqualTo("John");
        assertThat(users.get(0).surname()).isEqualTo("Doe");
    }

    @Test
    public void testGetUsersByLoginAndLastName() {
        List<User> users = userService.getAllUsers("jdoe", null, "Doe");
        assertThat(users).hasSize(1);
        assertThat(users.get(0).username()).isEqualTo("jdoe");
        assertThat(users.get(0).name()).isEqualTo("John");
        assertThat(users.get(0).surname()).isEqualTo("Doe");
    }

    @Test
    public void testGetUsersByFirstNameAndLastName() {
        List<User> users = userService.getAllUsers(null, "John", "Doe");
        assertThat(users).hasSize(1);
        assertThat(users.get(0).username()).isEqualTo("jdoe");
        assertThat(users.get(0).name()).isEqualTo("John");
        assertThat(users.get(0).surname()).isEqualTo("Doe");
    }

    @Test
    public void testGetUsersByLoginFirstNameAndLastName() {
        List<User> users = userService.getAllUsers("jdoe", "John", "Doe");
        assertThat(users).hasSize(1);
        assertThat(users.get(0).username()).isEqualTo("jdoe");
        assertThat(users.get(0).name()).isEqualTo("John");
        assertThat(users.get(0).surname()).isEqualTo("Doe");
    }
}