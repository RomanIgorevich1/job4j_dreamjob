package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class SimpleCandidateService implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final FileService fileService;

    public SimpleCandidateService(CandidateRepository sql2oCandidateService, FileService fileService) {
        this.candidateRepository = sql2oCandidateService;
        this.fileService = fileService;
    }

    @Override
    public Candidate save(Candidate candidate, FileDto image) {
        saveNewFile(candidate, image);
        return candidateRepository.save(candidate);
    }
    private void saveNewFile(Candidate candidate, FileDto image) {
        File file = fileService.save(image);
        candidate.setFileId(file.getId());
    }

    @Override
    public boolean update(Candidate candidate, FileDto image) {
        boolean isNewFile = image.getContent().length == 0;
        if (isNewFile) {
            return candidateRepository.update(candidate);
        }
        int oldFileId = candidate.getFileId();
        saveNewFile(candidate, image);
        boolean isUpdated = candidateRepository.update(candidate);
        fileService.deleteById(oldFileId);
        return isUpdated;
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Candidate> candidateOptional = findById(id);
        boolean result = false;
        if (candidateOptional.isPresent()) {
            candidateRepository.deleteById(id);
            fileService.deleteById(candidateOptional.get().getFileId());
            result = true;
        }
        return result;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}
