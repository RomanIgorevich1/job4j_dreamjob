package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class IndexControllerTest {
    @Test
    public void whenRequestIndexPageThenGetIndexPage() {
        IndexController index = new IndexController();
        assertThat(index.getIndex()).isEqualTo("index");
    }
}