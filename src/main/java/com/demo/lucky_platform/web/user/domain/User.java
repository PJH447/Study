package com.demo.lucky_platform.web.user.domain;

import com.demo.lucky_platform.web.common.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@EqualsAndHashCode(callSuper = true)
@Data(staticConstructor = "of")
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

    @Column(name = "email", columnDefinition = "varchar(255)", nullable = false)
    private String email;

    @Column(name = "name", columnDefinition = "varchar(16)")
    private String name;

    @Column(name = "nickname", columnDefinition = "varchar(16)")
    private String nickname;

    @Column(name = "birthday", columnDefinition = "varchar(50)")
    private String birthday;

    @Column(name = "picture", columnDefinition = "varchar(255)")
    private String picture;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", columnDefinition = "varchar(255)")
    private String password;

    @Column(name = "phone", columnDefinition = "varchar(15)")
    private String phone;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

}
