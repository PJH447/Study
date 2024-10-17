package com.demo.lucky_platform.web.user.domain;

import com.demo.lucky_platform.web.common.domain.BaseEntity;
import com.demo.lucky_platform.web.counselor.domain.Favorite;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Where;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Builder
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user", indexes = {})
public class User extends BaseEntity {

    private static final long serialVersionUID = 142151L;

    @Id
    @Column(name = "user_id", columnDefinition = "bigint(20)")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(name = "email", columnDefinition = "varchar(255)", nullable = false)
    private String email;

    @Size(min = 2)
    @Column(name = "name", columnDefinition = "varchar(16)")
    private String name;

    @Size(min = 2)
    @Column(name = "nickname", columnDefinition = "varchar(16)")
    private String nickname;

    @Column(name = "picture", columnDefinition = "varchar(255)")
    private String picture;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", columnDefinition = "varchar(255)")
    private String password;

    @Pattern(regexp = "^\\d{2,3}\\d{3,4}\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다.")
    @Column(name = "phone", columnDefinition = "varchar(15)")
    private String phone;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Builder.Default
    @Where(clause = "enabled = true")
    @BatchSize(size = 600)
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Favorite> favoriteList = new ArrayList<>();

    public String getEmail() {
        if (ObjectUtils.isEmpty(this.email)) {
            return "";
        }
        return email;
    }

    public String getName() {
        if (ObjectUtils.isEmpty(this.name)) {
            return "";
        }
        return name;
    }

    public String getNickname() {
        if (ObjectUtils.isEmpty(this.nickname)) {
            return "";
        }
        return nickname;
    }

    public String getPhone() {
        if (ObjectUtils.isEmpty(this.phone)) {
            return "";
        }

        return phone;
    }

    public void initRole(Role role) {
        this.roles = new HashSet<>(Collections.singletonList(role));
    }

    public void editNickname(String nickname) {
        this.nickname = nickname;
    }

    public void editPassword(String newPassword) {
        this.password = newPassword;
    }
}
