package com.demo.lucky_platform.web.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class UserDslRepositoryImpl implements UserDslRepository {

    private final JPAQueryFactory jpaQueryFactory;


}
