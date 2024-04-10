package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Candidate;

public interface CandidateRepository {
    Candidate save(Candidate candidate);

    boolean update(Candidate candidate);

    boolean deleteById(int id);
}
