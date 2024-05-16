package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@ThreadSafe
@Controller
@RequestMapping("/vacancies")
public class VacancyController {
    private final VacancyService vacancyService;
    private final CityService cityService;

    public VacancyController(VacancyService vacancyService, CityService cityService) {
        this.vacancyService = vacancyService;
        this.cityService = cityService;
    }

    private void getUser(Model model, HttpSession session) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        model.addAttribute("user", user);
    }

    @GetMapping
    public String getAll(Model model, HttpSession session) {
        model.addAttribute("vacancies", vacancyService.findAll());
        getUser(model, session);
        return "vacancies/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model, HttpSession session) {
        model.addAttribute("cities", cityService.findAll());
        getUser(model, session);
        return "vacancies/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Vacancy vacancy, @RequestParam MultipartFile file,
                         Model model, HttpSession session) {
        getUser(model, session);
        try {
            vacancyService.save(vacancy, new FileDto(file.getOriginalFilename(), file.getBytes()));
            return "redirect:/vacancies";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id, HttpSession session) {
        Optional<Vacancy> vacancyOptional = vacancyService.findById(id);
        getUser(model, session);
        if (vacancyOptional.isEmpty()) {
            model.addAttribute("message", "Вакансия с указанием идентификатора не найдена");
            return "errors/404";
        }
        model.addAttribute("vacancy", vacancyOptional.get());
        model.addAttribute("cities", cityService.findAll());
        return "vacancies/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Vacancy vacancy, @RequestParam MultipartFile file,
                         Model model, HttpSession session) {
        getUser(model, session);
        try {
            boolean isUpdated = vacancyService.update(vacancy, new FileDto(file.getOriginalFilename(), file.getBytes()));
            if (!isUpdated) {
                model.addAttribute("massage", "Вакансия с указанием идентификатора не найдена");
                return "errors/404";
            }
            return "redirect:/vacancies";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }

    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id, HttpSession session) {
        getUser(model, session);
        boolean isDeleted = vacancyService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("massage", "Вакансия с указанием идентификатора не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }
}