package com.demo.lucky_platform.web.counselor.domain;

import com.demo.lucky_platform.web.common.domain.BaseEntity;
import com.demo.lucky_platform.web.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

@Builder
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "favorite", indexes = {})
public class Favorite extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "favorite_id", columnDefinition = "bigint(20)")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Where(clause = "enabled = true")
    @JoinColumn(name = "counselor_id", columnDefinition = "bigint(20)")
    private Counselor counselor;

    @ManyToOne(fetch = FetchType.LAZY)
    @Where(clause = "enabled = true")
    @JoinColumn(name = "user_id", columnDefinition = "bigint(20)")
    private User user;


}
