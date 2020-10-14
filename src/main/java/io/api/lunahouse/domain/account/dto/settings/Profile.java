package io.api.lunahouse.domain.account.dto.settings;

import io.api.lunahouse.domain.account.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public class Profile {

    @Length(max = 35)
    private String statusMessage;

    @URL
    private String url;

    @Length(max = 255)
    private String team;

    @Length(max = 255)
    private String location;

    private String profileImage;

    public Profile(Account account) {
        this.statusMessage = account.getStatusMessage();
        this.url = account.getUrl();
        this.team = account.getTeam();
        this.location = account.getLocation();
        this.profileImage = account.getProfileImage();
    }
}
