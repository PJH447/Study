package com.demo.lucky_platform.web.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthenticatedUser implements UserDetails {

    private static final long serialVersionUID = 5352517488466981150L;

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private Boolean enabled;
    private String name;
    private String nickname;
    private String email;
    @JsonIgnore private String picture;
    @JsonIgnore private String password;
    private String phone;
    private Set<Role> roles;

    public AuthenticatedUser(User user) {
        this.id = user.getId();
        this.createdAt = user.getCreatedAt();
        this.lastUpdatedAt = user.getLastUpdatedAt();
        this.enabled = user.getEnabled();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.picture = user.getPicture();
        this.password = user.getPassword();
        this.phone = user.getPhone();
        this.roles = user.getRoles();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(this.getRoles());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return this.getEnabled();
    }

}
