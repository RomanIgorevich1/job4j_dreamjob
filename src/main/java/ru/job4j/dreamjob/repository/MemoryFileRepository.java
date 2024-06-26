package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.File;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class MemoryFileRepository implements FileRepository {
    private AtomicInteger nextId = new AtomicInteger(0);

    private final ConcurrentHashMap<Integer, File> files = new ConcurrentHashMap<>();

    @Override
    public File save(File file) {
        file.setId(nextId.incrementAndGet());
        return files.putIfAbsent(file.getId(), file);
    }

    @Override
    public Optional<File> findById(int id) {
        return Optional.ofNullable(files.get(id));
    }

    @Override
    public boolean deleteById(int id) {
        return files.remove(id) != null;
    }
}
