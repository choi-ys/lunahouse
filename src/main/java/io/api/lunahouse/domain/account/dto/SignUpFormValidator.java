package io.api.lunahouse.domain.account.dto;

import io.api.lunahouse.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    /**
     * 회원 가입 요청 시 email, engName 항목의 중복 여부 확인
     * @param object
     * @param errors
     */
    @Override
    public void validate(Object object, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) object;

        if(accountRepository.existsByEmail(signUpForm.getEmail())){
            errors.rejectValue("email", "invalid email", new Object[]{signUpForm.getEmail()}, "이미 사용 중인 이메일입니다.");
        }

        if(accountRepository.existsByEngName(signUpForm.getEngName())){
            errors.rejectValue("engName", "invalid engName", new Object[]{signUpForm.getEngName()}, "이미 사용 중인 영어 이름입니다.");
        }
    }
}
