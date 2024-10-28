package com.demo.lucky_platform.web;

import com.demo.lucky_platform.web.common.dto.CommonResponse;
import com.demo.lucky_platform.web.user.domain.AuthenticatedUser;
import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.repository.UserRepository;
import com.demo.lucky_platform.web.user.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final UserRepository userRepository;
    private final SecurityContextService securityContextService;

    @GetMapping("/v1")
    public CommonResponse test(@AuthenticationPrincipal AuthenticatedUser user) {
        RuntimeException runtimeException = new RuntimeException("hihih");
        String string = runtimeException.toString();
        System.out.println("string = " + string);

        String message = runtimeException.getMessage();
        System.out.println("message = " + message);
        return CommonResponse.createVoidResponse();
    }


    @GetMapping("/v2")
//    @PreAuthorize("isAuthenticated()")
    public CommonResponse test2(@AuthenticationPrincipal AuthenticatedUser user) {


        String result = "o---------k";
        return CommonResponse.createResponse(result);
    }


}
