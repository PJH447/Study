package com.demo.lucky_platform.web.chat.repository;

import com.demo.lucky_platform.web.chat.domain.Chat;
import com.demo.lucky_platform.web.user.domain.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.demo.lucky_platform.web.chat.domain.QChat.chat;
import static com.demo.lucky_platform.web.user.domain.QUser.user;

@RequiredArgsConstructor
public class ChatDslRepositoryImpl implements ChatDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Chat> findRecentChat(Long targetUserId, Pageable pageable) {

        List<Chat> chatList = jpaQueryFactory.select(chat)
                                             .from(chat)
                                             .leftJoin(chat.user, user).fetchJoin()
                                             .where(
                                                     chat.enabled.isTrue(),
                                                     chat.targetUser.id.eq(targetUserId)
                                             )
                                             .orderBy(chat.id.desc())
                                             .offset(pageable.getOffset())
                                             .limit(pageable.getPageSize() + 1)
                                             .fetch();

        boolean hasNext = false;
        if (chatList.size() > pageable.getPageSize()) {
            chatList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(chatList, pageable, hasNext);
    }

    @Override
    public List<Chat> findLastChatListByUser(Pageable pageable) {
        List<Long> targetChatIdList = jpaQueryFactory.select(chat.id.max())
                                                     .from(chat)
                                                     .where(chat.enabled.isTrue())
                                                     .groupBy(chat.targetUser)
                                                     .fetch();


        return jpaQueryFactory.select(chat)
                              .from(chat)
                              .leftJoin(chat.targetUser, user).fetchJoin()
                              .where(
                                      chat.id.in(targetChatIdList),
                                      user.enabled.isTrue()
                              )
                              .orderBy(chat.createdAt.desc())
                              .offset(pageable.getOffset())
                              .limit(pageable.getPageSize())
                              .fetch();
    }


}
