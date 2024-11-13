package com.demo.lucky_platform.config.other;

import com.demo.lucky_platform.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.springframework.messaging.simp.stomp.StompHeaderAccessor.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StompPreHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 메시지가 채널로 전송되기 전에 실행
     *
     * @param message 메시지 객체
     * @param channel 메시지 채널
     * @return 수정된 메시지 객체
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            List<String> headers = accessor.getNativeHeader("Authorization");
            String accessToken = headers.get(0).replace("Bearer ", "");
            if (CollectionUtils.isEmpty(headers) || !jwtTokenProvider.validateToken(accessToken)) {
                throw new MessageDeliveryException("UNAUTHORIZED");
            }
        }

        return message;
    }
}
