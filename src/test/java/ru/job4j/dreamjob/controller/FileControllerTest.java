package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileControllerTest {
    private FileService fileService;
    private FileController fileController;
    private MultipartFile testFile;
    @BeforeEach
    public void initService() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }
    @Test
    public void whenGetByIdThenFileFound() throws Exception {
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        when(fileService.getFindById(anyInt())).thenReturn(Optional.of(fileDto));
        var view = fileController.getById(anyInt()).getStatusCodeValue();
        assertThat(view).isEqualTo(200);
    }

    @Test
    public void whenGetByIdThenFileNotFound() {
        when(fileService.getFindById(anyInt())).thenReturn(Optional.empty());
        var view = fileController.getById(anyInt()).getStatusCodeValue();
        assertThat(view).isEqualTo(404);
    }
}