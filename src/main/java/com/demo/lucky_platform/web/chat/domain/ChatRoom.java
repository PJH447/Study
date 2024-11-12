package com.demo.lucky_platform.web.chat.domain;

import com.demo.lucky_platform.web.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "chat_room", indexes = {})
public class ChatRoom extends BaseEntity {

    @Id
    @Column(name = "chat_room_id", columnDefinition = "bigint(20)")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",columnDefinition = "varchar(20)")
    private String name;

}
