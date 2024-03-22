package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.MemoryCandidateRepository;
import ru.job4j.dreamjob.repository.SearchRepository;

@Controller
@RequestMapping("/candidates")
public class CandidateController {
    private final SearchRepository<Candidate> search = MemoryCandidateRepository.getInstance();

    @GetMapping
    public String getModel(Model model) {
        model.addAttribute("candidates", search.findAll());
        return "candidates/list";
    }
}
