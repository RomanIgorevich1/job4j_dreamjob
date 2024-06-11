package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;
import java.util.List;
import java.util.Optional;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CandidateControllerTest {
    private CandidateService candidateService;
    private CityService cityService;
    private CandidateController candidateController;
    private MultipartFile testFile;

    @BeforeEach
    public void initService() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestCandidateListPageThenGetPageWithCandidates() {
        var candidate1 = new Candidate(1, "name1", "desc1", now(), 1, 1);
        var candidate2 = new Candidate(2, "name2", "desc2", now(), 3, 2);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);
        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");
        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenRequestCandidatesCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);
        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var actualCandidates = model.getAttribute("cities");
        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCandidates).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostCandidateWithFileThenSameDataAndRedirectToCandidatesPage() throws Exception {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate);
        var model = new ConcurrentModel();
        var view = candidateController.create(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(candidateService.save(any(), any())).thenThrow(expectedException);
        var model = new ConcurrentModel();
        var view = candidateController.create(new Candidate(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");
        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenUpdateCandidate() throws Exception {
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var candidateArgumentCapture = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCapture = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCapture.capture(), fileDtoArgumentCapture.capture()))
                .thenReturn(true);
        var model = new ConcurrentModel();
        var view = candidateController.update(candidate, testFile, model);
        var actualCandidate = candidateArgumentCapture.getValue();
        var actualFileDto = fileDtoArgumentCapture.getValue();
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenDeleteCandidate() {
        var argument = ArgumentCaptor.forClass(Integer.class);
        when(candidateService.deleteById(argument.capture())).thenReturn(true);
        var model = new ConcurrentModel();
        var view = candidateController.delete(model, 2);
        var actualArgument = argument.getValue();
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualArgument).isEqualTo(2);
    }

    @Test
    public void whenErrDeleteCandidate() {
        when(candidateService.deleteById(anyInt())).thenReturn(false);
        var model = new ConcurrentModel();
        var view = candidateController.delete(model, anyInt());
        var actualArgument = model.getAttribute("message");
        assertThat(view).isEqualTo("errors/404");
        assertThat(actualArgument).isEqualTo("Кандидат с указанным идентификатором не найден");
    }

    @Test
    public void whenGetByIdCandidate() {
        var argument = ArgumentCaptor.forClass(Integer.class);
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var candidateOptional = Optional.of(candidate);
        when(candidateService.findById(argument.capture())).thenReturn(candidateOptional);
        var model = new ConcurrentModel();
        var view = candidateController.getById(model, 1);
        var actualArgument = argument.getValue();
        assertThat(view).isEqualTo("candidates/one");
        assertThat(actualArgument).isEqualTo(1);
    }
}
