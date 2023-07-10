package com.a503.churros.entity.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Table(name="token")
@Entity
public class Token extends DefaultTime {
    // 만약, createdTime, modifiedTime 넣고 싶으면 extends DefaultTime
    @Id
    @Column(name = "user_email", length = 255 , nullable = false)
    private String userEmail;

    @Column(name = "refresh_token", length = 1024 , nullable = false)
    private String refreshToken;

    public Token(){}

    public Token updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    @Builder
    public Token(String userEmail, String refreshToken, Date createdDate, Date expireDate) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.setCreatedDate(createdDate);
        this.setExpireDate(expireDate);
    }
}
