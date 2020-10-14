
package io.api.lunahouse.controller.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.api.lunahouse.config.WithAccount;
import io.api.lunahouse.domain.account.dto.settings.Profile;
import io.api.lunahouse.domain.account.entity.account.Account;
import io.api.lunahouse.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountRepository accountRepository;

    @AfterEach
    void cleanRepository(){
        accountRepository.deleteAll();
    }

    @WithAccount("noel")
    @Test
    @DisplayName("Update Profile : 프로필 수정 화면")
    public void profileSettingView() throws Exception {
        // When : Update Profile Post Request
        String urlTemplate = "/settings/profile";
        ResultActions resultActions = this.mockMvc.perform(get(urlTemplate)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .with(csrf())
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
        ;
    }

    @WithAccount("noel")
    @Test
    @DisplayName("Update Profile : 입력값이 없는 프로필 수정 요청")
    public void profileUpdate_Empty_Parameter() throws Exception {
        // When : Update Profile Post Request
        String urlTemplate = "/settings/profile";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf())
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/noel"))
                .andExpect(flash().attributeExists("message"))
        ;

        Account createdTestAccount = accountRepository.findByEngName("noel");
        assertNull(createdTestAccount.getStatusMessage());
        assertNull(createdTestAccount.getUrl());
        assertNull(createdTestAccount.getTeam());
        assertNull(createdTestAccount.getLocation());
    }

    @WithAccount("noel")
    @Test
    @DisplayName("Update Profile : 입력값이 잘못된 프로필 수정 요청")
    public void profileUpdate_Wrong_Parameter() throws Exception {
        // Given
        String url = "wrongUrl";

        // When : Update Profile Post Request
        String urlTemplate = "/settings/profile";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("url", url)
                        .with(csrf())
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors())
        ;

        Account createdTestAccount = accountRepository.findByEngName("noel");
        assertNull(createdTestAccount.getStatusMessage());
    }

    @WithAccount("noel")
    @Test
    @DisplayName("Update Profile : 프로필 수정 요청")
    public void profileUpdate() throws Exception {
        // Given
        String statusMessage = "상태 메세지";
        String url = "https://www.github.com/choi-ys";
        String team = "스마트플러스 팀";
        String location = "서초구";

        Profile profile = new Profile();
        profile.setStatusMessage(statusMessage);
        profile.setUrl(url);
        profile.setTeam(team);
        profile.setLocation(location);

        // When : Update Profile Post Request
        String urlTemplate = "/settings/profile";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(profile))
                .param("statusMessage", statusMessage)
                .param("url", url)
                .with(csrf())
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/noel"))
                .andExpect(flash().attributeExists("message"))
        ;

        Account createdTestAccount = accountRepository.findByEngName("noel");
        assertEquals(statusMessage, createdTestAccount.getStatusMessage());
        assertEquals(url, createdTestAccount.getUrl());
    }
}