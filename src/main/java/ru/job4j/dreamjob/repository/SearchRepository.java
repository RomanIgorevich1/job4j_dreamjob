package ru.job4j.dreamjob.repository;

import java.util.Collection;
import java.util.Optional;

public interface SearchRepository<T> {
    Optional<T> findById(int id);
    Collection<T> findAll();
}
