package com.demo.lucky_platform.web.chat.domain;

import com.demo.lucky_platform.web.common.domain.BaseEntity;
import com.demo.lucky_platform.web.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "chat", indexes = {})
public class Chat extends BaseEntity {

    @Id
    @Column(name = "chat_id", columnDefinition = "bigint(20)")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "target_user_id", columnDefinition = "bigint(20)")
    private Long targetUserId;

    public void validateEmptyMessage() {
        if (this.message.isBlank()) {
            throw new RuntimeException("메세지가 비어있음");
        }
    }
}
