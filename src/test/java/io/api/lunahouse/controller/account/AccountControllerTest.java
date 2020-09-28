package io.api.lunahouse.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.api.lunahouse.domain.account.dto.SignUpForm;
import io.api.lunahouse.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
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

    @MockBean
    JavaMailSender javaMailSender;


    @DisplayName("Account : 회원 가입 화면 출력")
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
        ;
    }

    @DisplayName("Account : 입력값이 잘못된 요청")
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
        ;

    }

    @DisplayName("Account : 회원 가입 요청")
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
        ;

        assertThat(accountRepository.existsByEmail(email));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}