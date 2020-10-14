package io.api.lunahouse.controller.login;

import io.api.lunahouse.domain.account.dto.account.SignUpForm;
import io.api.lunahouse.domain.account.entity.account.Account;
import io.api.lunahouse.repository.AccountRepository;
import io.api.lunahouse.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @BeforeEach
    public void setUpParameters(){
        // Given : Create Account
        String engName = "noel";
        String email = "rcn115@naver.com";
        String password = "chldydtjr1!";

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEngName(engName);
        signUpForm.setEmail(email);
        signUpForm.setPassword(password);
        Account createdAccount = accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    public void clearParameters(){
        this.accountRepository.deleteAll();
    }

    @DisplayName("Login : 영어 이름을 이용한 로그인 요청")
    @Test
    public void loginWithEngName() throws Exception {
        // When : login request to user engName
        String urlTemplate = "/login";
        String engName = "noel";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
            .characterEncoding(StandardCharsets.UTF_8.name())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .param("username", engName)
            .param("password", "chldydtjr1!")
            .with(csrf())
        );

        // Then
        resultActions.andDo(print())
                .andExpect(authenticated().withUsername(engName))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
        ;
    }

    @DisplayName("Login : 이메일을 이용한 로그인 요청")
    @Test
    public void loginWithEmail() throws Exception {
        // When : login request to user email
        String urlTemplate = "/login";
        String email = "rcn115@naver.com";
        String engName = "noel";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("username", email)
                .param("password", "chldydtjr1!")
                .with(csrf())
        );

        // Then
        resultActions.andDo(print())
                .andExpect(authenticated().withUsername(engName))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
        ;
    }

    @DisplayName("Login : 입력값이 없는 요청")
    @Test
    public void login_EmptyRequest() throws Exception {
        String urlTemplate = "/login";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
        );

        resultActions.andDo(print())
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
        ;
    }

    @DisplayName("Login : 입력값이 잘못된 요청")
    @Test
    public void login_WrongParameter() throws Exception {
        String urlTemplate = "/login";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("username","choi-ys")
                .param("password", "chldydtjr1!")
                .with(csrf())
        );

        resultActions.andDo(print())
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
        ;
    }

    @DisplayName("Logout")
    @Test
    public void logout() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/logout")
                .with(csrf())
        );

        resultActions.andDo(print())
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
        ;
    }
}