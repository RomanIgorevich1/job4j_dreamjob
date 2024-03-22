package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

public interface VacancyRepository {
    Vacancy save(Vacancy vacancy);
    void deleteById(int id);
    boolean update(Vacancy vacancy);
}
