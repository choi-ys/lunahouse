package io.api.lunahouse.domain.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpForm {

    private String nickname;

    private String email;

    private String password;
}
