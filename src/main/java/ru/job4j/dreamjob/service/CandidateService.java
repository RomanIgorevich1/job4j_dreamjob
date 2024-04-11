package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Optional;

public interface CandidateService {

    Candidate save(Candidate candidate);

    boolean update(Candidate candidate);

    boolean deleteById(int id);

    Optional<Candidate> findById(int id);

    Collection<Candidate> findAll();
}
