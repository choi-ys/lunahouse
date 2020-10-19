package io.api.lunahouse.domain.account.dto.settings;

import io.api.lunahouse.domain.account.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class Notifications {


    @NotNull
    private boolean eventCreatedByEmail;

    @NotNull
    private boolean eventCreatedByWeb;

    @NotNull
    private boolean eventEnrollmentResultByEmail;

    @NotNull
    private boolean eventEnrollmentResultByWeb;

    @NotNull
    private boolean eventUpdatedByEmail;

    @NotNull
    private boolean eventUpdatedByWeb;

    public Notifications(Account account) {
        this.eventCreatedByWeb = account.isEventCreatedByWeb();
        this.eventCreatedByEmail = account.isEventCreatedByEmail();

        this.eventEnrollmentResultByWeb = account.isEventEnrollmentResultByWeb();
        this.eventEnrollmentResultByEmail = account.isEventEnrollmentResultByEmail();

        this.eventUpdatedByWeb = account.isEventUpdatedByWeb();
        this.eventUpdatedByEmail = account.isEventUpdatedByEmail();
    }

}
