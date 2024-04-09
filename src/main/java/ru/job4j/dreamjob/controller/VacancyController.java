package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.SearchRepository;
import ru.job4j.dreamjob.repository.MemoryVacancyRepository;

@Controller
@RequestMapping("/vacancies")
public class VacancyController {

    private final SearchRepository<Vacancy> search = MemoryVacancyRepository.getInstance();

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("vacancies", search.findAll());
        return "vacancies/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "vacancies/create";
    }
}
