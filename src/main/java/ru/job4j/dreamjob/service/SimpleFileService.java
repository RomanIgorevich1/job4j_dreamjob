package ru.job4j.dreamjob.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.repository.FileRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
public class SimpleFileService implements FileService {
    private final FileRepository fileRepository;
    private final String storageDirectory;

    public SimpleFileService(FileRepository sql2oFileRepository,
                             @Value("${file.directory}") String storageDirectory) {
        this.fileRepository = sql2oFileRepository;
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    private void createStorageDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public File save(FileDto fileDto) {
        String path = getNewFilePath(fileDto.getName());
        writeFileBytes(path, fileDto.getContent());
        return fileRepository.save(new File(fileDto.getName(), path));
    }

    private String getNewFilePath(String sourceName) {
        return storageDirectory + java.io.File.separator + UUID.randomUUID() + sourceName;
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Optional<FileDto> getFindById(int id) {
        Optional<File> fileDtoOptional = fileRepository.findById(id);
        if (fileDtoOptional.isEmpty()) {
            return Optional.empty();
        }
        byte[] content = readFileAsBytes(fileDtoOptional.get().getPath());
        return Optional.of(new FileDto(fileDtoOptional.get().getName(), content));
    }

    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException exception) {
            throw new RuntimeException();
        }
    }

    @Override
    public void deleteById(int id) {
        Optional<File> fileOptional = fileRepository.findById(id);
        if (fileOptional.isPresent()) {
            deleteFile(fileOptional.get().getPath());
            fileRepository.deleteById(id);
        }
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}