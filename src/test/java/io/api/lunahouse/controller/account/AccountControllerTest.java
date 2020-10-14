package io.api.lunahouse.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.api.lunahouse.domain.account.dto.account.SignUpForm;
import io.api.lunahouse.domain.account.entity.account.Account;
import io.api.lunahouse.repository.AccountRepository;
import io.api.lunahouse.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @MockBean
    JavaMailSender javaMailSender;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUpRepository(){
        accountRepository.deleteAll();
    }

    @DisplayName("SignUp : 회원 가입 화면 출력")
    @Test
    void signUpForm() throws Exception {

        // When
        String urlTemplate = "/sign-up";
        ResultActions resultActions = this.mockMvc.perform(get(urlTemplate)
            .characterEncoding(StandardCharsets.UTF_8.name())
            .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        //Then
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("account/sign-up"))
            .andExpect(model().attributeExists("signUpForm"))
            .andExpect(unauthenticated())
        ;
    }

    @DisplayName("SignUp : 입력값이 잘못된 이메일 인증 요청")
    @Test
    void checkEmailToken_wrongParameter() throws Exception {
        // Given
        String token = "";
        String email = "";

        String urlTemplate = "/check-email-token";
        ResultActions resultActions = this.mockMvc.perform(get(urlTemplate)
                .param("token", token)
                .param("email", email)
        );

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated())
        ;
    }

    @DisplayName("SignUp : 이메일 인증 요청")
    @Test
    void checkEmailToken() throws Exception {
        // Given
        String engName = "noel";
        String email = "yschoi@lunasoft.co.kr";
        String password = "chldydtjr1!";

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail(email);
        signUpForm.setEngName(engName);
        signUpForm.setPassword(password);

        Account createdAccount = accountService.processNewAccount(signUpForm);
        createdAccount.getEmailCheckToken();

        String urlTemplate = "/check-email-token";
        ResultActions resultActions = this.mockMvc.perform(get(urlTemplate)
                .param("token", createdAccount.getEmailCheckToken())
                .param("email", email)
        );

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(authenticated().withUsername(engName))
        ;
    }


    @DisplayName("SignUp : 입력값이 잘못된 회원 가입 요청")
    @Test
    void signUpSubmit_wrongParameter_request() throws Exception {
        // Given
        String email = "rcn115";
        String engName = "noel";
        String password = "chldydtjr1!";

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail(email);
        signUpForm.setEngName(engName);
        signUpForm.setPassword(password);

        // When
        String urlTemplate = "/sign-up";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(signUpForm))
                .with(csrf())
        );

        // Then
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("account/sign-up"))
            .andExpect(unauthenticated())
        ;

    }

    @DisplayName("SignUp : 회원 가입 요청")
    @Test
    void signUpSubmit() throws Exception {
        // Given
        String email = "rcn115@naver.com";
        String engName = "noel";
        String password = "chldydtjr1!";

        // When
        String urlTemplate = "/sign-up";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                .param("email", email)
                .param("engName", engName)
                .param("password", password)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername(engName))
        ;

        Account account = accountRepository.findByEmail(email);
        assertThat(account).isNotNull();
        assertThat(account.getPassword()).isNotEqualTo(password); // 비밀번호 암호화 여부 확인
        assertThat(account.getEmailCheckToken()).isNotNull();

        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}