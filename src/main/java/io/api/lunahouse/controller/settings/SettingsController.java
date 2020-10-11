package io.api.lunahouse.controller.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.api.lunahouse.domain.account.dto.Profile;
import io.api.lunahouse.domain.account.entity.Account;
import io.api.lunahouse.service.AccountService;
import io.api.lunahouse.util.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;

    public static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    public static final String SETTINGS_PROFILE_URL = "/settings/profile";

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        if(account == null){
            model.addAttribute("error", "유효하지 못한 사용자의 요청입니다.");
            return "redirect:/";
        }

        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account
                                , @Valid Profile profile
                                , Errors errors
                                , Model model
                                , RedirectAttributes redirectAttributes
    ) throws JsonProcessingException {
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }
        accountService.updateProfile(account, profile);
        redirectAttributes.addFlashAttribute("message", "프로필 수정이 완료되었습니다.");
        return "redirect:/profile/"+account.getEngName();
    }

}
