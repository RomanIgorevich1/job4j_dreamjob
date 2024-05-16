package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import static java.util.Optional.empty;
import ru.job4j.dreamjob.model.User;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oUserRepository.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");
        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(dataSource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void deleteFile() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }

    }

    @Test
    public void whenSaveThenGetSame() {
        User user1 = sql2oUserRepository.save(
                new User(0, "email1", "name1", "password1")).get();
        User savedUser = sql2oUserRepository.findByEmailAndPassword(user1.getEmail(), user1.getPassword()).get();
        assertThat(savedUser).isEqualTo(user1);
    }

    @Test
    public void whenSaveSeveralThenGetSame() {
        User user1 = sql2oUserRepository.save(
                new User(0, "email1", "name1", "password1")).get();
        User user2 = sql2oUserRepository.save(
                new User(0, "email2", "name2", "password2")).get();
        User user3 = sql2oUserRepository.save(
                new User(0, "email3", "name3", "password3")).get();
        User savedUser = sql2oUserRepository.findByEmailAndPassword(user2.getEmail(), user2.getPassword()).get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user2);

    }

    @Test
    public void whenSameEmailThenErr() {
        User user1 = sql2oUserRepository.save(
                new User(0, "email1", "name1", "password1")).get();
        assertThat(sql2oUserRepository.findByEmailAndPassword(user1.getEmail(), user1.getPassword())).isNotEmpty();
        assertThat(sql2oUserRepository.save(
                new User(0, "email1", "name2", "password2"))).isEqualTo(empty());
    }
}