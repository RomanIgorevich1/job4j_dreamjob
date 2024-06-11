package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserService userService;
    private  UserController userController;

    private HttpServletRequest servletRequest;
    @BeforeEach
    public void initService() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        servletRequest = new MockHttpServletRequest();
    }

    @Test
    public void whenGetRegistrationPage() {
        assertThat(userController.getRegistrationPage()).isEqualTo("users/register");
    }

    @Test
    public void whenRegister() {
        var user = new User(1, "email", "name", "password");
        var argument = ArgumentCaptor.forClass(User.class);
        when(userService.save(argument.capture())).thenReturn(Optional.of(user));
        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualUser = argument.getValue();
        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenRegisterThenUserNotFound() {
        var argument = ArgumentCaptor.forClass(User.class);
        when(userService.save(argument.capture())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();
        var view = userController.register(model, any());
        var actualArgument = model.getAttribute("message");
        assertThat(view).isEqualTo("users/register");
        assertThat(actualArgument).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    public void whenLoginUser() {
       var user = new User(1, "email", "name", "password");
       when(userService.findByEmailAndPassword(any(), any()))
               .thenReturn(Optional.of(user));
       var model = new ConcurrentModel();
       var view = userController.loginUser(user, model, servletRequest);
       var actualRequest = servletRequest.getSession().getAttribute("user");
       assertThat(view).isEqualTo("redirect:/vacancies");
       assertThat(actualRequest).isEqualTo(user);
    }

    @Test
    public void whenPasswordOrEmailWrongThenCantLogin() {
        var user = new User(1, "email", "name", "password");
        when(userService.findByEmailAndPassword(any(), any()))
                .thenReturn(Optional.empty());
        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, servletRequest);
        var actualMessage = model.getAttribute("error");
        assertThat(view).isEqualTo("users/login");
        assertThat(actualMessage).isEqualTo("Почта или пароль введены неверно");
    }
}
