package ru.job4j.dreamjob.repository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;
import javax.sql.DataSource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sql2oCandidateRepositoryTest {
    private static Sql2oCandidateRepository sql2oCandidateRepository;
    private static Sql2oFileRepository sql2oFileRepository;

    private static  LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);

    private static File file;

    @BeforeAll
    public static void initRepositories() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oCandidateRepository.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);

        sql2oCandidateRepository = new Sql2oCandidateRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }
    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clearCandidates() {
        sql2oCandidateRepository.findAll().forEach(candidate -> sql2oCandidateRepository.deleteById(candidate.getId()));
    }

    @Test
    public void whenSaveCandidate() {
        Candidate candidate = sql2oCandidateRepository.save(
                new Candidate(0, "name", "description1", creationDate,
                        1, file.getId()));
        Candidate saveCandidate = sql2oCandidateRepository.findById(candidate.getId()).get();
        assertThat(saveCandidate).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        Candidate candidate1 = sql2oCandidateRepository.save(new Candidate(0, "Ivan",
                "description1", creationDate, 1, file.getId()));
        Candidate candidate2 = sql2oCandidateRepository.save(new Candidate(0, "Roman",
                "description2", creationDate, 2, file.getId()));
        Candidate candidate3 = sql2oCandidateRepository.save(new Candidate(0, "Maks",
                "description3", creationDate, 3, file.getId()));
        Candidate candidate4 = sql2oCandidateRepository.save(new Candidate(0, "Igor",
                "description4", creationDate, 2, file.getId()));
        assertThat(sql2oCandidateRepository.findAll()).
                isEqualTo(List.of(candidate1, candidate2, candidate3, candidate4));
    }
    @Test
    public void whenNoCandidatesThenTableEmpty() {
        assertThat(sql2oCandidateRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oCandidateRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteCandidateTableEmpty() {
        Candidate candidate = sql2oCandidateRepository.save(new Candidate(0, "Roman",
                "description1", creationDate, 2, file.getId()));
        boolean isDeleted = sql2oCandidateRepository.deleteById(candidate.getId());
        Optional<Candidate> saveCandidate = sql2oCandidateRepository.findById(candidate.getId());
        assertThat(isDeleted).isTrue();
        assertThat(saveCandidate).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oCandidateRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        Candidate candidate  = sql2oCandidateRepository.save(new Candidate(0, "Roman",
                        "description1", creationDate, 2, file.getId()));
        Candidate updatedCandidate = new Candidate(candidate.getId(), "Ivan",
                "new description", creationDate, candidate.getCityId(), file.getId()
        );
        var isUpdated = sql2oCandidateRepository.update(updatedCandidate);
        var savedVacancy = sql2oCandidateRepository.findById(updatedCandidate.getId()).get();
        assertThat(isUpdated).isTrue();
        assertThat(savedVacancy).usingRecursiveComparison().isEqualTo(updatedCandidate);
    }

    @Test
    public void whenUpdateUnExistingVacancyThenGetFalse() {
        Candidate candidate = new Candidate(0, "Roman",
                "description1", creationDate, 2, file.getId());
        boolean isUpdate = sql2oCandidateRepository.update(candidate);
        assertThat(isUpdate).isFalse();
    }
}