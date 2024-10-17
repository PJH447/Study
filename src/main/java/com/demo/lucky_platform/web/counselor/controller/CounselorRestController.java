package com.demo.lucky_platform.web.counselor.controller;

import com.demo.lucky_platform.web.common.dto.CommonResponse;
import com.demo.lucky_platform.web.counselor.dto.CounselorDto;
import com.demo.lucky_platform.web.counselor.service.CounselorService;
import com.demo.lucky_platform.web.user.domain.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CounselorRestController {

    private final CounselorService counselorService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/v1/counselor/{counselorId}")
    public CommonResponse getCounselor(
            @PathVariable("counselorId") Long counselorId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        Long userId = user == null ? null : user.getId();
        CounselorDto counselorDto = counselorService.findCounselor(counselorId, userId);

        return CommonResponse.createResponse(counselorDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/v1/favorite/{counselorId}")
    public CommonResponse erollFavorite(
            @PathVariable("counselorId") Long counselorId,
            @AuthenticationPrincipal AuthenticatedUser user) {

        counselorService.createFavorite(user.getId(), counselorId);
        return CommonResponse.createVoidResponse();
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/v1/favorite/{counselorId}")
    public CommonResponse cancelFavorite(
            @PathVariable("counselorId") Long counselorId,
            @AuthenticationPrincipal AuthenticatedUser user) {

        counselorService.deleteFavorite(user.getId(), counselorId);
        return CommonResponse.createVoidResponse();
    }
}