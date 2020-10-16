package io.api.lunahouse.domain.account.dto.settings;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class PasswordForm {

    @NotNull
    @Length(min = 8, max = 50)
    private String newPassword;

    @NotNull
    @Length(min = 8, max = 50)
    private String newPasswordConfirm;
}
