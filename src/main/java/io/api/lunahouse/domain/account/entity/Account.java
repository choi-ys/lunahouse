package io.api.lunahouse.domain.account.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    // Email
    @Column(unique = true)
    private String email;

    // 영어이름
    @Column(unique = true)
    private String engName;

    // 비밀번호
    private String password;

    // 이메일 인증 여부
    private boolean emailVerified;

    // 이메일 검증 시 사용할 토큰
    private String emailCheckToken;

    // 이메일 인증 토큰 발급 시간
    private LocalDateTime emailCheckToeknGeneratedAt;

    // 가입일자
    private LocalDateTime joinedAt;

    // 사용자 프로필 정보
    private String statusMessage; // 상태 메세지
    private String url; // 개인 URL
    private String team; // 소속 Team
    private String location; // 위치

    // 프로필 이미지
    @Lob // String Type의 기본 Column은 varchar(255)로 설정되므로, 이보다 길어질 수 있는 URL항목은 @Lob을 이용 하여 Text로 Mapping 되도록 설정
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    // 이벤트 생성 시 이메일 수신 여부
    private boolean eventCreatedByEmail;

    // 이벤트 생성 시 웹 수신 여부
    private boolean eventCreatedByWeb;

    // 이벤트 등록 시 이메일 수신 여부
    private boolean eventEnrollmentResultByEmail;

    // 이벤트 등록 시 웹 수신 여부
    private boolean eventEnrollmentResultByWeb;

    // 이벤트 갱신 시 이메일 수신 여부
    private boolean eventUpdatedResultByEmail;

    // 이벤트 갱신 시 웹 수신 여부
    private boolean eventUpdatedResultByWeb;

    public void generateEmailToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckToeknGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckToeknGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}
