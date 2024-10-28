package com.demo.lucky_platform.web.user.dto;

import com.demo.lucky_platform.web.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HeaderInfoDto {

    private String email;
    private String name;
    private String nickname;
    private String picture;
    private String phone;
    private Boolean isAdmin;
    private Boolean isVip;

    public static HeaderInfoDto from(User user) {
        return HeaderInfoDto.builder()
                            .email(user.getEmail())
                            .name(user.getName())
                            .nickname(user.getNickname())
                            .picture(user.getPicture())
                            .phone(user.getPhone())
                            .isAdmin(user.isAdmin())
                            .isVip(user.isVip())
                            .build();
    }
}
